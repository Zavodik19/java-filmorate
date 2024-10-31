package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserDbStorageTest {
    private static final Integer TEST_USER_ID = 1;
    private static final Integer TEST_USER_ID_2 = 2;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private UserStorage userStorage;
    private User user1, user2;

    @BeforeEach
    public void init() {
        userStorage = new UserDbStorage(jdbcTemplate);

        user1 = new User();
        user1.setId(TEST_USER_ID);
        user1.setEmail("user1@example.com");
        user1.setLogin("user1login");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        user2 = new User();
        user2.setId(TEST_USER_ID_2);
        user2.setEmail("user2@example.com");
        user2.setLogin("user2login");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1985, 6, 15));
    }

    @Test
    @DisplayName("Проверка добавления пользователя")
    public void check_addUser_shouldAddUser() {
        User newUser = userStorage.addUser(user1);
        assertEquals(user1, newUser);
    }

    @Test
    @DisplayName("Проверка обновления пользователя")
    public void check_updateUser_shouldUpdateUser() {
        userStorage.addUser(user1);
        user1.setName("Updated User One");
        user1.setEmail("updated_user1@example.com");

        userStorage.updateUser(user1);
        assertEquals(user1, userStorage.getUserById(TEST_USER_ID).get());
    }

    @Test
    @DisplayName("Проверка удаления пользователя")
    public void check_deleteUser_shouldDeleteUser() {
        userStorage.addUser(user1);
        userStorage.deleteUser(TEST_USER_ID);
        assertThrows(NoSuchElementException.class, () -> userStorage.getUserById(TEST_USER_ID).get());
    }

    @Test
    @DisplayName("Проверка получения всех пользователей")
    public void check_getAllUsers_shouldReturnAllUsers() {
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        Collection<User> users = userStorage.getAllUsers();
        assertEquals(List.of(user1, user2), users);
    }

    @Test
    @DisplayName("Проверка получения пользователя по ID")
    public void check_getUserById_shouldReturnUserById() {
        userStorage.addUser(user1);
        User retrievedUser = userStorage.getUserById(TEST_USER_ID).orElseThrow();

        assertEquals(user1, retrievedUser);
    }

    @Test
    @DisplayName("Проверка добавления друга")
    public void check_addFriend_shouldAddFriend() {
        userStorage.addUser(user1);
        userStorage.addUser(user2);

        userStorage.addFriend(user1.getId(), user2.getId());

        Collection<User> friends = userStorage.getUserFriends(user1.getId());
        assertEquals(1, friends.size());
        assertEquals(user2, friends.iterator().next());
    }

    @Test
    @DisplayName("Проверка удаления друга")
    public void check_removeFriend_shouldRemoveFriend() {
        userStorage.addUser(user1);
        userStorage.addUser(user2);

        userStorage.addFriend(user1.getId(), user2.getId());
        userStorage.removeFriend(user1.getId(), user2.getId());

        Collection<User> friends = userStorage.getUserFriends(user1.getId());
        assertEquals(0, friends.size());
    }

    @Test
    @DisplayName("Проверка получения друзей пользователя")
    public void check_getUserFriends_shouldReturnFriends() {
        userStorage.addUser(user1);
        userStorage.addUser(user2);

        userStorage.addFriend(user1.getId(), user2.getId());

        Collection<User> friends = userStorage.getUserFriends(user1.getId());
        assertEquals(1, friends.size());
        assertEquals(user2, friends.iterator().next());
    }

    @Test
    @DisplayName("Проверка получения общих друзей")
    public void check_getCommonFriends_shouldReturnCommonFriends() {
        User user3 = new User();
        user3.setId(3);
        user3.setEmail("user3@example.com");
        user3.setLogin("user3login");
        user3.setName("User Three");
        user3.setBirthday(LocalDate.of(1980, 5, 20));

        userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addUser(user3);

        userStorage.addFriend(user1.getId(), user2.getId());
        userStorage.addFriend(user1.getId(), user3.getId());
        userStorage.addFriend(user2.getId(), user3.getId());

        Collection<User> commonFriends = userStorage.getCommonFriends(user1.getId(), user2.getId());
        assertEquals(1, commonFriends.size());
        assertEquals(user3, commonFriends.iterator().next());
    }

}
