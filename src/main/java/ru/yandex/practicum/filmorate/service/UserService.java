package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

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

    public User getUserById(int id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addFriend(Integer userId, Integer friendId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + friendId + " не найден"));

        // Проверка, не является ли пользователь самим собой
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Нельзя добавить себя в друзья");
        }

        userStorage.addFriend(userId, friendId);
        log.info("Пользователь с ID {} добавил в друзья пользователя с ID {}", userId, friendId);

        return user;
    }


    public void removeFriend(Integer userId, Integer friendId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + friendId + " не найден"));

        Collection<User> userFriends = userStorage.getUserFriends(userId);

        if (userFriends != null && userFriends.stream().anyMatch(u -> u.getId().equals(friendId))) {
            log.info("Пользователь с ID {} удалил из друзей пользователя с ID {}", userId, friendId);
            userStorage.removeFriend(userId, friendId);
            return;
        } else {
            log.warn("Друг с ID {} не найден у пользователя с ID {}", friendId, userId);
            return;
        }
    }

    public Collection<User> getFriends(Integer userId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден."));
        log.info("Получаем друзей для пользователя с ID: {}", userId);

        return userStorage.getUserFriends(userId);
    }

    public Collection<User> getCommonFriends(Integer userId1, Integer userId2) {
        User user1 = userStorage.getUserById(userId1)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId1 + " не найден."));
        User user2 = userStorage.getUserById(userId2)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId2 + " не найден."));

        return userStorage.getCommonFriends(userId1, userId2);
    }
}
