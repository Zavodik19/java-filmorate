package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private long currentId = 1;

    // добавление фильма
    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        validateFilm(film);
        film.setFilmId(currentId++);
        films.put(film.getFilmId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    // получение всех фильмов
    @GetMapping
    public Collection<Film> allFilms() {
        log.info("Получение списка фильмов");
        return films.values();
    }

    // обновление фильма
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        validateFilm(newFilm);
        if (!films.containsKey(newFilm.getFilmId())) {
            throw new ValidationException("Фильм с таким ID не найден");
        }
        films.put(newFilm.getFilmId(), newFilm);
        log.info("Фильм обновлен: {}", newFilm);
        return newFilm;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription() == null || film.getDescription().isEmpty()) {
            throw new ValidationException("Описание фильма не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания фильма - 200 символов.");
        }
        if (film.getReleaseDate() == null ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28).plusDays(1))) {
            throw new ValidationException("Дата выхода фильма не может быть раньше 28 декабря 1895 года.");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }
    }
}
