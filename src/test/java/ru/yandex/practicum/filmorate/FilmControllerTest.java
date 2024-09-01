package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    @DisplayName("Проверка на создание фильма с валидными данными")
    void addFilmShouldAddFilmWhenValidFilm() {
        Film film = new Film(null, "Название фильма", "Описание",
                LocalDate.of(2022, 1, 1), 120);
        Film addedFilm = filmController.addFilm(film);

        assertNotNull(addedFilm.getId());
        assertEquals("Название фильма", addedFilm.getName());
    }

    @Test
    @DisplayName("Проверка на создание фильма с пустым названием")
    void addFilmShouldThrowExceptionWhenFilmNameIsEmpty() {
        Film film = new Film(null, "", "Описание",
                LocalDate.of(2022, 1, 1), 120);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.addFilm(film));
        assertEquals("Название фильма не может быть пустым.", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка на создание фильма с пустым описанием")
    void addFilmShouldThrowExceptionWhenDescriptionIsEmpty() {
        Film film = new Film(null, "Название фильма", "",
                LocalDate.of(2022, 1, 1), 120);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.addFilm(film));
        assertEquals("Описание фильма не может быть пустым.", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка на создание фильма с некорректной датой выхода")
    void addFilmShouldThrowExceptionWhenReleaseDateIsInvalid() {
        Film film = new Film(null, "Название фильма", "Описание",
                LocalDate.of(1895, 12, 28), 120);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.addFilm(film));
        assertEquals("Дата выхода фильма не может быть раньше 28 декабря 1895 года.",
                exception.getMessage());
    }

    @Test
    @DisplayName("Проверка на создание фильма с отрицательной продолжительностью")
    void addFilmShouldThrowExceptionWhenDurationIsNegative() {
        Film film = new Film(null, "Название фильма", "Описание",
                LocalDate.of(2022, 1, 1), -120);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.addFilm(film));
        assertEquals("Продолжительность фильма должна быть положительным числом.",
                exception.getMessage());
    }

    @Test
    @DisplayName("Проверка на обновление фильма с сохранением изменений")
    void updateFilmShouldUpdateFilmWhenValidFilm() {
        Film film = new Film(null, "Название фильма", "Описание",
                LocalDate.of(2022, 1, 1), 120);
        Film addedFilm = filmController.addFilm(film);
        addedFilm.setName("Название фильма обновлено");

        Film updatedFilm = filmController.updateFilm(addedFilm);

        assertEquals("Название фильма обновлено", updatedFilm.getName());
    }

    @Test
    @DisplayName("Проверка обновление фильма с несуществующим ID")
    void updateFilmShouldThrowExceptionWhenFilmNotFound() {
        Film film = new Film(999L, "Название фильма", "Описание",
                LocalDate.of(2022, 1, 1), 120);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.updateFilm(film));
        assertEquals("Фильм с таким ID не найден", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка на хранение и возвращение всего списка фильмов")
    void allFilmsShouldReturnAllFilms() {
        filmController.addFilm(new Film(null, "Film 1", "Описание",
                LocalDate.of(2022, 1, 1), 120));
        filmController.addFilm(new Film(null, "Film 2", "Описание",
                LocalDate.of(2022, 1, 2), 150));

        assertEquals(2, filmController.allFilms().size());
    }
}