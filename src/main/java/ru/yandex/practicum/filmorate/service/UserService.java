package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (!userStorage.getUserById(user.getId()).isPresent()) {
            throw new NotFoundException("Пользователь не найден с ID: " + user.getId());
        }
        return userStorage.updateUser(user);
    }

    public Optional<User> getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + friendId + " не найден"));

        // Проверка, не является ли пользователь самим собой
        if (userId == friendId) {
            throw new IllegalArgumentException("Нельзя добавить себя в друзья");
        }

        // Проверка, есть ли уже друг в списке друзей
        if (!user.getFriends().contains(friendId)) {
            user.getFriends().add(friendId);
            friend.getFriends().add(userId);

            userStorage.updateUser(user);
            userStorage.updateUser(friend);

            log.info("Пользователь с ID {} добавил в друзья пользователя с ID {}", userId, friendId);
        } else {
            throw new IllegalArgumentException("Пользователь с ID " + friendId + " уже в друзьях");
        }
        return user;
    }


    public User removeFriend(int userId, int friendId) {
        Optional<User> userOpt = userStorage.getUserById(userId);
        Optional<User> friendOpt = userStorage.getUserById(friendId);

        if (userOpt.isPresent() && friendOpt.isPresent()) {
            User user = userOpt.get();
            User friend = friendOpt.get();
            Set<Integer> userFriends = user.getFriends();
            Set<Integer> friendFriends = friend.getFriends();

            userFriends.remove(friendId);
            friendFriends.remove(userId);

            return user;
        }   else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public List<User> getFriends(int userId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден."));
        log.info("Получаем друзей для пользователя с ID: {}", userId);

        if (user.getFriends().isEmpty()) {
            return Collections.emptyList();
        }

        return user.getFriends().stream()
                .map(friendId -> userStorage.getUserById(friendId)
                        .orElseThrow(() -> new NotFoundException("Друг с ID " + friendId + " не найден.")))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId1, int userId2) {
        User user1 = userStorage.getUserById(userId1)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId1 + " не найден."));
        User user2 = userStorage.getUserById(userId2)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId2 + " не найден."));

        // Получаем общих друзей
        return user1.getFriends().stream()
                .filter(user2.getFriends()::contains)
                .map(friendId -> userStorage.getUserById(friendId)
                        .orElseThrow(() -> new NotFoundException("Друг с ID " + friendId + " не найден.")))
                .collect(Collectors.toList());
    }
}
