package ru.yandex.practicum.filmorate.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_GET_GENRE = "SELECT genre_id, name FROM genres WHERE genre_id = ?";
    private static final String SQL_GET_ALL_GENRES = "SELECT genre_id, name FROM genres";

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenreById(int id) {
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_GET_GENRE, id);
            if (rowSet.next()) {
                return new Genre(
                        rowSet.getInt("genre_id"),
                        rowSet.getString("name")
                );
            } else {
                throw new GenreNotFoundException(String.format("Жанр по идентификатору %d не найден.", id));
            }
        } catch (DataAccessException e) {
            throw new GenreNotFoundException(String.format("Ошибка доступа к данным для жанра с идентификатором %d.", id), e);
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query(SQL_GET_ALL_GENRES, (rs, rowNum) -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("name"))
        );
    }


}
