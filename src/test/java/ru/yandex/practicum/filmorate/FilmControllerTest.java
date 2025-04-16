package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    FilmController filmController;
    Film film;
    Validator validator;

    @BeforeEach
    void beforeEach() {
        filmController = new FilmController();
        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(60);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void createFilmTest() {
        filmController.create(film);
        assertEquals(1, filmController.findAll().size(), "Ошибка в создании фильма");
    }

    @Test
    void updateFilmTest() {
        Film newFilm = filmController.create(film);
        newFilm.setDuration(200);
        Film filmTest = filmController.update(newFilm);
        assertTrue(filmController.findAll().contains(filmTest), "Ошибка обновления фильма");
    }

    @Test
    void blankNameTest() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
        film.setName(" ");
        Set<ConstraintViolation<Film>> violations2 = validator.validate(film);
        assertFalse(violations2.isEmpty());
    }

    @Test
    void dateEarlyBorderTest() {
        film.setReleaseDate(LocalDate.parse("1894-12-28"));
        Exception exception = assertThrows(ValidateException.class, () -> filmController.create(film));
        assertEquals("Дата должна быть позднее 28 декабря 1985 г.", exception.getMessage());
    }

    @Test
    void badDurationTest() {
        film.setDuration(0);
        Exception exception = assertThrows(ValidateException.class, () -> filmController.create(film));
        assertEquals("Продолжительность должна быть положительным числом", exception.getMessage());
        film.setDuration(-1);
        exception = assertThrows(ValidateException.class, () -> filmController.create(film));
        assertEquals("Продолжительность должна быть положительным числом", exception.getMessage());
    }

    @Test
    void longDescriptionTest() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
        String longLine = "line";
        for (int i = 0; i <= 200; i++) {
            longLine = longLine + i;
        }
        film.setDescription(longLine);
        Set<ConstraintViolation<Film>> violations2 = validator.validate(film);
        assertFalse(violations2.isEmpty());
    }

    @Test
    void createFilmWithNullFieldTest() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
        film.setReleaseDate(null);
        Set<ConstraintViolation<Film>> violations2 = validator.validate(film);
        assertFalse(violations2.isEmpty());
    }

    @Test
    void unknownFilmUpdate() {
        Film testFilm = filmController.create(film);
        testFilm.setId(100);
        Exception e = assertThrows(NotFoundException.class, () -> filmController.update(testFilm));
        assertEquals("Такого фильма нет в нашем списке", e.getMessage());
    }
}
