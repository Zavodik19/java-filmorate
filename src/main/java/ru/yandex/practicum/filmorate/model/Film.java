package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private Integer id;

    @NotEmpty(message = "Название фильма не может быть пустым.")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания фильма — 200 символов")
    private String description;

    @NotNull(message = "Дата релиза не может быть пустой")
    @PastOrPresent(message = "Дата релиза не может быть в будущем")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private long duration;

    private Set<Integer> likes = new HashSet<>();
    private Mpa mpa;
    private Set<Genre> genres = new HashSet<>();

    public Integer getMpaId() {
        return mpa != null ? mpa.getId() : null;
    }

}
