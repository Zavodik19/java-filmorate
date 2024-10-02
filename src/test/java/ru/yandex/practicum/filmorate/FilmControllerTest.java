package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FilmControllerTest {

    private FilmController filmController;
    private FilmService filmService;

    @BeforeEach
    void setUp() {
        filmService = Mockito.mock(FilmService.class);
        filmController = new FilmController(filmService);
    }

    @Test
    @DisplayName("Проверка на создание фильма с валидными данными")
    void addFilmShouldAddFilmWhenValidFilm() {
        Film film = new Film(null, "Название фильма", "Описание",
                LocalDate.of(2022, 1, 1), 120);
        when(filmService.addFilm(film)).thenReturn(film);

        Film addedFilm = filmController.addFilm(film).getBody();

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
    @DisplayName("Проверка на обновление фильма с сохранением изменений")
    void updateFilmShouldUpdateFilmWhenValidFilm() {
        Film film = new Film(1L, "Название фильма", "Описание",
                LocalDate.of(2022, 1, 1), 120);
        when(filmService.updateFilm(film)).thenReturn(film);

        Film updatedFilm = filmController.updateFilm(film).getBody();

        assertEquals("Название фильма", updatedFilm.getName());
    }

    @Test
    @DisplayName("Проверка обновления фильма с несуществующим ID")
    void updateFilmShouldThrowExceptionWhenFilmNotFound() {
        Film film = new Film(999L, "Название фильма", "Описание",
                LocalDate.of(2022, 1, 1), 120);

        when(filmService.updateFilm(film)).thenThrow(new ValidationException("Фильм с таким ID не найден"));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.updateFilm(film));
        assertEquals("Фильм с таким ID не найден", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка на хранение и возвращение всего списка фильмов")
    void allFilmsShouldReturnAllFilms() {
        when(filmService.getTopFilms(10)).thenReturn(List.of(
                new Film(1L, "Film 1", "Описание", LocalDate.of(2022, 1, 1), 120),
                new Film(2L, "Film 2", "Описание", LocalDate.of(2022, 1, 2), 150)
        ));

        List<Film> films = filmController.getPopularFilms(10).getBody();

        assertEquals(2, films.size());
    }
}