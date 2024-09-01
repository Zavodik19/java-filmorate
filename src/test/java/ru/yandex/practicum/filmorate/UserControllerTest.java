package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    @DisplayName("Проверка на валидность данных добавляемого пользователя")
    void addUserShouldAddUserWhenValidUser() {
        User user = new User(0, "test@example.com", "testLogin", "testUser",
                LocalDate.of(2000, 1, 1));
        User addedUser = userController.addUser(user);

        assertNotNull(addedUser.getId());
        assertEquals("testLogin", addedUser.getLogin());
    }

    @Test
    @DisplayName("Проверка на добавление пользователя с некорректным email")
    void addUserShouldThrowExceptionWhenEmailIsInvalid() {
        User user = new User(0, "invalidEmail", "testLogin", "testUser",
                LocalDate.of(2000, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.addUser(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @.",
                exception.getMessage());
    }

    @Test
    @DisplayName("Проверка на корректное обновление пользователя")
    void updateUserShouldUpdateUserWhenValidUser() {
        User user = new User(0, "test@example.com", "testLogin", "testUser",
                LocalDate.of(2000, 1, 1));
        User addedUser = userController.addUser(user);
        addedUser.setName("Пользователь обновлен");

        User updatedUser = userController.update(addedUser);

        assertEquals("Пользователь обновлен", updatedUser.getName());
    }

    @Test
    @DisplayName("Проверка обновление пользователя с несуществующим ID")
    void updateUserShouldThrowExceptionWhenUserNotFound() {
        User user = new User(999L, "test@example.com", "testLogin", "testUser",
                LocalDate.of(2000, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.update(user));
        assertEquals("Пользователь с таким ID не найден", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка на корректное хранение и возвращение всех пользователей")
    void allUsersShouldReturnAllUsers() {
        userController.addUser(new User(0, "user1@example.com", "user1", "User One",
                LocalDate.of(1999, 2, 1)));
        userController.addUser(new User(0, "user2@example.com", "user2", "User Two",
                LocalDate.of(2000, 4, 2)));

        assertEquals(2, userController.allUsers().size());
    }
}