package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        if (!isValidEmail(user.getEmail())) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
        }
        User addedUser = userService.addUser(user);
        return ResponseEntity.ok(addedUser);
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@");
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        log.info("Обновление пользователя: {}", user);
        return ResponseEntity.ok(userService.updateUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriend(id, friendId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.removeFriend(id, friendId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<User>> getFriends(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getFriends(id));
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<Set<Long>> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return ResponseEntity.ok(userService.getCommonFriends(id, otherId));
    }
}
