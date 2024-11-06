package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;


    public Film addFilm(Film film) {
        isValidReleaseDate(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (!filmStorage.getFilmById(film.getId()).isPresent()) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        isValidReleaseDate(film);
        return filmStorage.updateFilm(film);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Integer id) {
        Film film = filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
        film.setMpa(mpaStorage.getMpaById(film.getMpaId())
                .orElseThrow(() -> new NotFoundException("MPA с id " + film.getMpaId() + " не найден")));

        Set<Genre> filmGenres = new HashSet<>();
        for (Integer genreId : film.getGenres().stream().map(Genre::getId).collect(Collectors.toSet())) {
            Genre foundGenre = genreStorage.getGenreById(genreId);
            if (foundGenre != null) {
                filmGenres.add(foundGenre);
            }
        }
        film.setGenres(filmGenres);
        return film;
    }

    public Film addLike(Integer filmId, Integer userId) {
        userStorage.getUserById(userId);
        Film film = getFilmById(filmId);
        filmStorage.addLike(filmId, userId);
        return film;
    }

    public Film removeLike(Integer filmId, Integer userId) {
        userStorage.getUserById(userId);
        Film film = getFilmById(filmId);
        filmStorage.removeLike(filmId, userId);
        return film;
    }

    public List<Film> getTopFilms(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Количество фильмов должно быть больше нуля.");
        }

        return filmStorage.getTopFilms(count);
    }

    private void isValidReleaseDate(Film film) {
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new IllegalArgumentException("Дата выхода фильма должна быть позже 28 декабря 1895 года");
        }
    }
}