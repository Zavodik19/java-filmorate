package ru.yandex.practicum.filmorate.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {  // Класс перенесен из пакета controller в exceptions
    private String error;
    private String message;
}
