package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final Map<Long, Set<Long>> filmLikes = new HashMap<>();

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Long id) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            throw new ValidationException("Фильм не найден.");
        }
        return film;
    }

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new ValidationException("Фильм не найден.");
        }
        filmLikes.putIfAbsent(filmId, new HashSet<>());
        if (!filmLikes.get(filmId).add(userId)) {
            throw new ValidationException("Пользователь уже поставил лайк этому фильму.");
        }
    }

    public void removeLike(Long filmId, Long userId) {
        Set<Long> likes = filmLikes.get(filmId);
        if (likes == null || !likes.remove(userId)) {
            throw new ValidationException("Лайк не найден.");
        }
    }


    public List<Film> getTopFilms(int count) {
        List<Map.Entry<Long, Set<Long>>> entries = new ArrayList<>(filmLikes.entrySet());
        entries.sort((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()));

        List<Film> topFilms = new ArrayList<>();
        for (int i = 0; i < Math.min(count, entries.size()); i++) {
            topFilms.add(filmStorage.getFilmById(entries.get(i).getKey()));
        }
        return topFilms;
    }
}