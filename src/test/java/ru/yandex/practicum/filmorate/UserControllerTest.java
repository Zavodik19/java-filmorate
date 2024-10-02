package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserController userController;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    @DisplayName("Проверка на валидность данных добавляемого пользователя")
    void addUserShouldAddUserWhenValidUser() {
        User user = new User(0L, "test@example.com", "testLogin", "testUser",
                LocalDate.of(2000, 1, 1), new HashSet<>());
        when(userService.addUser(user)).thenReturn(user);

        User addedUser = userController.addUser(user).getBody();

        assertNotNull(addedUser.getId());
        assertEquals("testLogin", addedUser.getLogin());
    }

    @Test
    @DisplayName("Проверка на добавление пользователя с некорректным email")
    void addUserShouldThrowExceptionWhenEmailIsInvalid() {
        User user = new User(0L, "invalidEmail", "testLogin", "testUser",
                LocalDate.of(2000, 1, 1), new HashSet<>());
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.addUser(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @.",
                exception.getMessage());
    }

    @Test
    @DisplayName("Проверка на корректное обновление пользователя")
    void updateUserShouldUpdateUserWhenValidUser() {
        User user = new User(1L, "test@example.com", "testLogin", "testUser",
                LocalDate.of(2000, 1, 1), new HashSet<>());
        when(userService.updateUser(user)).thenReturn(user);

        User updatedUser = userController.updateUser(user).getBody();

        assertEquals("testUser", updatedUser.getName());
    }

    @Test
    @DisplayName("Проверка обновления пользователя с несуществующим ID")
    void updateUserShouldThrowExceptionWhenUserNotFound() {
        User user = new User(999L, "test@example.com", "testLogin", "testUser",
                LocalDate.of(2000, 1, 1), new HashSet<>());

        when(userService.updateUser(user)).thenThrow(new ValidationException("Пользователь с таким ID не найден"));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.updateUser(user));
        assertEquals("Пользователь с таким ID не найден", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка на получение друзей пользователя")
    void getFriendsShouldReturnUserFriends() {
        User user1 = new User(1L, "user1@example.com", "user1", "User One", LocalDate.of(1999, 2, 1), new HashSet<>());
        User user2 = new User(2L, "user2@example.com", "user2", "User Two", LocalDate.of(2000, 4, 2), new HashSet<>());

        when(userService.getFriends(1L)).thenReturn(List.of(user2));

        List<User> friends = userController.getFriends(1L).getBody();

        assertEquals(1, friends.size());
        assertEquals("user2@example.com", friends.get(0).getEmail());
    }
}