package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;

@RestControllerAdvice
public class ExceptionHandler {

    public ErrorResponse validateExceptionHandle(ValidateException e) {
        return new ErrorResponse("Ошибка Валидации", e.getMessage());
    }

    public ErrorResponse conditionsExceptionHandle(ConditionsNotMetException e) {
        return new ErrorResponse("Ошибка", e.getMessage());
    }

    public ErrorResponse notFoundExceptionHandler(NotFoundException e) {
        return new ErrorResponse("Ошибка", e.getMessage());
    }
}
