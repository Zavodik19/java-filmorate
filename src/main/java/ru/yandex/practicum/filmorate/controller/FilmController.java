package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        log.info("Добавление фильма: {}", film);
        return ResponseEntity.status(HttpStatus.CREATED).body(filmService.addFilm(film));
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        log.info("Обновление фильма: {}", film);
        return ResponseEntity.ok(filmService.updateFilm(film));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilm(@PathVariable Long id) {
        return ResponseEntity.ok(filmService.getFilmById(id));
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return ResponseEntity.ok(filmService.getTopFilms(count));
    }
}