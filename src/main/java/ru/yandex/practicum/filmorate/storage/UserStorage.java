package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    Collection<User> getAllUsers();

    Optional<User> getUserById(Integer id);

    void deleteUser(Integer id);

    void addFriend(Integer userId, Integer friendId);

    void removeFriend(Integer userId, Integer friendId);

    Collection<User> getUserFriends(Integer userId);

    Collection<User> getCommonFriends(Integer userId, Integer friendId);
}
