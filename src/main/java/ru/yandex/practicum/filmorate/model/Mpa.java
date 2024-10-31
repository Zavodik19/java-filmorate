package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Mpa implements Comparable<Mpa> {
    private Integer id;

    private String name;

    @Override
    public int compareTo(Mpa other) {
        return this.id.compareTo(other.id);
    }

    public Mpa(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}