package ru.yandex.practicum.filmorate.dao;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Film addFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        String genreSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        if (film.getName() == null || film.getDescription() == null || film.getReleaseDate() == null || film.getMpa() == null) {
            throw new IllegalArgumentException("Фильм содержит неинициализированные поля.");
        }
        if (film.getName().isEmpty() || film.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Название и описание фильма не могут быть пустыми.");
        }
        validateGenres(film.getGenres());

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, film.getName());
                ps.setString(2, film.getDescription());
                ps.setDate(3, Date.valueOf(film.getReleaseDate()));
                ps.setLong(4, film.getDuration());
                ps.setInt(5, film.getMpa().getId());
                return ps;
            }, keyHolder);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Невозможно добавить фильм: рейтинг MPA с ID " + film.getMpa().getId() + " не существует.", e);
        }

        // Получение ID созданного фильма
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        // Добавление связей жанров
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(genreSql, film.getId(), genre.getId());
        }

        return film;
    }

    private boolean genreExists(Integer genreId) {
        String sql = "SELECT COUNT(*) FROM genres WHERE genre_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{genreId}, Integer.class);
        return count != null && count > 0;
    }

    private void validateGenres(Set<Genre> genres) {
        for (Genre genre : genres) {
            if (!genreExists(genre.getId())) {
                throw new IllegalArgumentException("Жанр с ID " + genre.getId() + " не существует.");
            }
        }
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()), film.getDuration(), film.getMpa().getId(), film.getId());
        return film;
    }

    @Override
    public void deleteFilm(int id) {
        String sql = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        String genreSql = "SELECT g.genre_id, g.name FROM genres g " +
                "JOIN film_genre fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ?";

        return jdbcTemplate.query(sql, new Object[]{id}, rs -> {
            if (rs.next()) {
                Film film = new Film();
                film.setId(rs.getInt("film_id"));
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                film.setDuration(rs.getLong("duration"));
                film.setMpa(new Mpa(rs.getInt("mpa_id"), null));

                List<Genre> genres = jdbcTemplate.query(genreSql, new Object[]{id}, (rsGenre, rowNum) -> {
                    int genreId = rsGenre.getInt("genre_id");
                    String genreName = rsGenre.getString("name");
                    return new Genre(genreId, genreName);
                });

                film.setGenres(new HashSet<>(genres));
                return Optional.of(film);
            }
            return Optional.empty();
        });
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("film_id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setMpa(new Mpa(rs.getInt("mpa_id"), null));
            return film;
        });
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        String sql = "INSERT INTO film_likes (user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        String sql = "DELETE FROM film_likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public List<Film> getTopFilms(int count) {
        String sql = "SELECT f.*, m.rating_mpa_id AS mpa_id, m.name AS mpa_name, COUNT(fl.user_id) AS likes_count " +
                "FROM films f " +
                "LEFT JOIN film_likes fl ON f.film_id = fl.film_id " +
                "JOIN MPA m ON f.mpa_id = m.rating_mpa_id " +
                "GROUP BY f.film_id, m.rating_mpa_id, m.name " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, new Object[]{count}, (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("film_id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getLong("duration"));
            film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
            return film;
        });
    }
}