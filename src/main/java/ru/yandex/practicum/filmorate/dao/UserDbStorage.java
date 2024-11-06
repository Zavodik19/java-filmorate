package ru.yandex.practicum.filmorate.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    // Статические переменные для SQL-команд
    private static final String SQL_INSERT_USER = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE_USER = "UPDATE users SET name = ?, email = ?, login = ?, birthday = ? WHERE user_id = ?";
    private static final String SQL_DELETE_USER = "DELETE FROM users WHERE user_id = ?";
    private static final String SQL_GET_USER = "SELECT * FROM users WHERE user_id = ?";
    private static final String SQL_GET_ALL_USERS = "SELECT * FROM users";
    private static final String SQL_INSERT_FRIEND = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
    private static final String SQL_GET_FRIENDS = "SELECT friend_id FROM friends WHERE user_id = ?";

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERT_USER, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        int generatedId = keyHolder.getKey().intValue();
        user.setId(generatedId);

        return user;
    }

    @Override
    public User updateUser(User user) {
        jdbcTemplate.update(SQL_UPDATE_USER, user.getName(), user.getEmail(), user.getLogin(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public void deleteUser(Integer id) {
        jdbcTemplate.update(SQL_DELETE_USER, id);
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        return jdbcTemplate.query(SQL_GET_USER, new Object[]{id}, rs -> {
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("user_id"));
                user.setEmail(rs.getString("email"));
                user.setLogin(rs.getString("login"));
                user.setName(rs.getString("name"));
                user.setBirthday(rs.getDate("birthday").toLocalDate());
                return Optional.of(user);
            }
            return Optional.empty();
        });
    }

    @Override
    public List<User> getAllUsers() {
        return jdbcTemplate.query(SQL_GET_ALL_USERS, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt("user_id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            return user;
        });
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        jdbcTemplate.update(SQL_INSERT_FRIEND, userId, friendId);
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        String sqlDeleteFromUser = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        String sqlDeleteFromFriend = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";

        // Удаляем связь со стороны пользователя
        jdbcTemplate.update(sqlDeleteFromUser, userId, friendId);

        // Удаляем связь с обратной стороны (если существует)
        jdbcTemplate.update(sqlDeleteFromFriend, friendId, userId);
    }

    public Collection<User> getUserFriends(Integer userId) {
        List<Integer> friendIds = jdbcTemplate.query(SQL_GET_FRIENDS, (rs, rowNum) -> rs.getInt("friend_id"), userId);
        return friendIds.stream()
                .map(this::getUserById)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getCommonFriends(Integer userId1, Integer userId2) {
        String sql = "SELECT friend_id FROM friends WHERE user_id = ? " +
                "INTERSECT " +
                "SELECT friend_id FROM friends WHERE user_id = ?";
        List<Integer> commonFriendIds = jdbcTemplate.query(sql, new Object[]{userId1, userId2}, (rs, rowNum) -> rs.getInt("friend_id"));

        return commonFriendIds.stream()
                .map(this::getUserById)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }
}