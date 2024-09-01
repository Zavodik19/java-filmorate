package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private long currentId = 1;

    // создание пользователя
    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        validateUser(user);
        user.setId(currentId++);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    // получение списка всех пользователей
    @GetMapping
    public Collection<User> allUsers() {
        log.info("Получение списка пользователей");
        return users.values();
    }

    // обновление пользователя
    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        validateUser(newUser);
        if (!users.containsKey(newUser.getId())) {
            throw new ValidationException("Пользователь с таким ID не найден");
        }
        users.put(newUser.getId(), newUser);
        log.info("Пользователь обновлен: {}", newUser);
        return newUser;
    }

    private void validateUser(User user) {
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
        }
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
