package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public User addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user == null || friend == null) {
            throw new ValidationException("Пользователь не найден.");
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        return user;
    }

    public User removeFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user == null || friend == null) {
            throw new ValidationException("Пользователь не найден.");
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        return user;
    }

    public List<User> getFriends(Long userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new ValidationException("Пользователь не найден.");
        }
        return userStorage.getAllUsers().stream()
                .filter(friend -> user.getFriends().contains(friend.getId()))
                .toList();
    }


    public Set<Long> getCommonFriends(Long userId, Long otherUserId) {
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherUserId);
        if (user == null || otherUser == null) {
            throw new ValidationException("Пользователь не найден.");
        }
        Set<Long> commonFriends = new HashSet<>(user.getFriends());
        commonFriends.retainAll(otherUser.getFriends());
        return commonFriends;
    }
}
