package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private static final int MAX_DESCRIPTION_SIZE = 200;
    private static final LocalDate EARLY_DATE = LocalDate.parse("1985-12-28");

    @GetMapping
    public List<Film> findAll() {
        return films.values().stream().toList();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        validate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм добавлен в список");
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        if (film == null) {
            log.warn("Объект пустой");
            return film;
        }
        validate(film);

        if (film.getId() == null) {
            log.warn("ConditionsNotMetException");
            throw new ConditionsNotMetException("Id должен быть заполнен");
        } else if (!films.containsKey(film.getId())) {
            log.warn("NotFoundException");
            throw new NotFoundException("Такого фильма нет в нашем списке");
        }

        films.put(film.getId(), film);
        log.info("Фильм обновлен");
        return film;
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("ValidationException");
            throw new ValidateException("Название не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > MAX_DESCRIPTION_SIZE) {
            log.warn("ValidationException");
            throw new ValidateException("Превышена длина описания");
        }

        if (film.getReleaseDate() == null) {
            log.warn("Дата релиза пустая");
            throw new ValidateException("Дата должна быть заполнена");
        } else if(film.getReleaseDate().isBefore(EARLY_DATE)) {
            log.warn("ValidationException");
            throw new ValidateException("Дата должна быть позднее 28 декабря 1985 г.");
        }

        if (film.getDuration() <= 0) {
            log.warn("ValidationException");
            throw new ValidateException("Продолжительность должна быть положительным числом");
        }
    }

    private Integer getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
