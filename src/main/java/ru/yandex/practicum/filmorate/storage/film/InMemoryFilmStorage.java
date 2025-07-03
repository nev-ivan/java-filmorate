package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private static final int MAX_DESCRIPTION_SIZE = 200;
    private static final LocalDate EARLY_DATE = LocalDate.parse("1895-12-28");

    public Film create(Film film) {
        validate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм добавлен в список");
        return film;
    }

    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    public Film getFilm(int id) {
        return Optional.ofNullable(films.get(id))
                .orElseThrow(() -> new NotFoundException("Такого фильма нет в нашем списке"));
    }

    public Film update(Film film) {
        if (film == null) {
            log.warn("Объект пустой");
            return film;
        }

        if (film.getId() == null) {
            log.warn("id должен быть указан");
            throw new ConditionsNotMetException("Id должен быть заполнен");
        } else if (!films.containsKey(film.getId())) {
            log.warn("Такого фильма нет в нашем списке");
            throw new NotFoundException("Такого фильма нет в нашем списке");
        }

        Film newFilm = films.get(film.getId());

        if (film.getName() != null && !film.getName().isBlank()) {
            newFilm.setName(film.getName());
        }

        if (film.getDescription() != null && film.getDescription().length() < MAX_DESCRIPTION_SIZE) {
            newFilm.setDescription(film.getDescription());
        }

        if (film.getReleaseDate() != null && film.getReleaseDate().isAfter(EARLY_DATE)) {
            newFilm.setDescription(film.getDescription());
        }

        if (film.getDuration() > 0) {
            newFilm.setDuration(film.getDuration());
        }

        films.put(film.getId(), film);
        log.info("Фильм обновлен");
        return film;
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(EARLY_DATE)) {
            log.warn("ValidateException");
            throw new ValidateException("Дата должна быть позднее 28 декабря 1985 г.");
        }

        if (film.getDuration() <= 0) {
            log.warn("ValidationException");
            throw new ValidateException("Продолжительность должна быть положительным числом");
        }
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
