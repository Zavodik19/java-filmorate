package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Genre implements Comparable<Genre> {
    private Integer id;
    private String name;

    @Override
    public int compareTo(Genre other) {
        return this.id.compareTo(other.id);
    }

    public Genre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
