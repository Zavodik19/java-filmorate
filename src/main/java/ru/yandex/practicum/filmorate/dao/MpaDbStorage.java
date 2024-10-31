package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage implements MpaStorage {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Mpa> getMpaById(int id) {
        String sql = "SELECT * FROM MPA WHERE rating_mpa_id = ?";
        return jdbcTemplate.query(sql, new Object[]{id}, resultSet -> {
            if (resultSet.next()) {
                return Optional.of(new Mpa(resultSet.getInt("rating_mpa_id"), resultSet.getString("name")));
            }
            return Optional.empty();
        });
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT rating_mpa_id AS mpa_id, name FROM MPA ORDER BY rating_mpa_id;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Mpa(rs.getInt("mpa_id"), rs.getString("name")));
    }
}