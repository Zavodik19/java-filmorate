package ru.yandex.practicum.filmorate.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorResponse handleValidation(final MethodArgumentNotValidException e) {
        logger.warn("Ошибка валидации: {}", e.getMessage());
        return new ErrorResponse("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorResponse handleIllegalArgument(final IllegalArgumentException e) {
        logger.warn("Ошибка: {}", e.getMessage());
        return new ErrorResponse("Ошибка", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public ErrorResponse handleNotFound(final NotFoundException e) {
        logger.warn("Объект не найден: {}", e.getMessage());
        return new ErrorResponse("Объект не найден", e.getMessage());
    }

    @ExceptionHandler() // 500
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Exception e) {
        logger.error("Сервер не смог обработать запрос: {}", e.getMessage(), e);
        return new ErrorResponse("Cервер не смог обработать запрос", e.getMessage());
    }

    @ExceptionHandler(GenreNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public ErrorResponse handleGenreNotFound(final GenreNotFoundException e) {
        logger.warn("Жанр не найден: {}", e.getMessage());
        return new ErrorResponse("Жанр не найден", e.getMessage());
    }

}