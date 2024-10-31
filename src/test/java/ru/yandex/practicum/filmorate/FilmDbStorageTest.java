package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmDbStorageTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private FilmStorage filmStorage;
    private Film testFilm1;
    private Film testFilm2;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("DELETE FROM films");
        filmStorage = new FilmDbStorage(jdbcTemplate);
        testFilm1 = new Film();
        testFilm1.setName("Test Film 1");
        testFilm1.setDescription("Description for test film 1");
        testFilm1.setReleaseDate(LocalDate.of(2000, 1, 1));
        testFilm1.setDuration(120);
        testFilm1.setMpa(new Mpa(1, "G"));

        testFilm2 = new Film();
        testFilm2.setName("Test Film 2");
        testFilm2.setDescription("Description for test film 2");
        testFilm2.setReleaseDate(LocalDate.of(2005, 5, 5));
        testFilm2.setDuration(150);
        testFilm2.setMpa(new Mpa(2, "PG"));
    }

    @Test
    @DisplayName("Проверка добавления фильма")
    public void testAddFilm() {
        Film savedFilm = filmStorage.addFilm(testFilm1);
        assertNotNull(savedFilm.getId(), "Фильм должен иметь ID после добавления");
        assertEquals(testFilm1.getName(), savedFilm.getName());
    }

    @Test
    @DisplayName("Проверка обновления фильма")
    public void testUpdateFilm() {
        Film savedFilm = filmStorage.addFilm(testFilm1);
        savedFilm.setName("Updated Test Film 1");
        Film updatedFilm = filmStorage.updateFilm(savedFilm);

        assertEquals("Updated Test Film 1", updatedFilm.getName());
        assertEquals(savedFilm.getId(), updatedFilm.getId(), "ID должен оставаться прежним после обновления");
    }

    @Test
    @DisplayName("Проверка удаления фильма")
    public void testDeleteFilm() {
        Film savedFilm = filmStorage.addFilm(testFilm1);
        filmStorage.deleteFilm(savedFilm.getId());

        assertThrows(NoSuchElementException.class, () -> filmStorage.getFilmById(savedFilm.getId()).orElseThrow());
    }

    @Test
    @DisplayName("Проверка получения всех фильмов")
    public void testGetAllFilms() {
        filmStorage.addFilm(testFilm1);
        filmStorage.addFilm(testFilm2);

        List<Film> films = (List<Film>) filmStorage.getAllFilms();
        assertEquals(2, films.size(), "Должно быть добавлено два фильма");
        assertTrue(films.stream().anyMatch(film -> film.getName().equals("Test Film 1")));
        assertTrue(films.stream().anyMatch(film -> film.getName().equals("Test Film 2")));
    }
}
