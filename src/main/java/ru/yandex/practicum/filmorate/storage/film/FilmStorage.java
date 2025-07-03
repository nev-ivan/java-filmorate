package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component
public interface FilmStorage {
    public Film create(Film film);

    public List<Film> findAll();

    public Film update(Film film);

    public Film getFilm(int id);
}
