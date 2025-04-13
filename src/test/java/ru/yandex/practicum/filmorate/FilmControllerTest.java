package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    FilmController filmController;
    Film film;

    @BeforeEach
    void beforeEach() {
        filmController = new FilmController();
        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(60);
    }

    @Test
    void createFilmTest() {
        film.setName(" ");
        Exception exception = assertThrows(ValidateException.class, () -> filmController.create(film));
        assertEquals("Название не может быть пустым", exception.getMessage());
        film.setName("name");
        film.setReleaseDate(LocalDate.parse("1984-12-28"));
        exception = assertThrows(ValidateException.class, () -> filmController.create(film));
        assertEquals("Дата должна быть позднее 28 декабря 1985 г.", exception.getMessage());
        film.setReleaseDate(LocalDate.now());
        film.setDuration(0);
        exception = assertThrows(ValidateException.class, () -> filmController.create(film));
        assertEquals("Продолжительность должна быть положительным числом", exception.getMessage());
    }

    @Test
    void updateFilmTest() {
        filmController.create(film);
        Film film1 = new Film(film.getId(), " ", "description", LocalDate.parse("1994-12-28"), 20);
        assertThrows(ValidateException.class, () -> filmController.update(film1));
        Film film2 = new Film(film.getId(), film.getName(), "description", LocalDate.parse("1984-12-28"), 20);
        assertThrows(ValidateException.class, () -> filmController.update(film2));
        Film film3 = new Film(film.getId(), film.getName(), "description", film.getReleaseDate(), 0);
        assertThrows(ValidateException.class, () -> filmController.update(film3));
    }
}
