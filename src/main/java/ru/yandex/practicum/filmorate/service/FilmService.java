package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;



    public void doLike(int filmId, int userId) {
        checkId(filmId);
        checkUser(userId);
        Film film = filmStorage.getFilm(filmId);
        Set<Integer> likes = film.getLikes();
        likes.add(userId);
        film.setLikes(likes);
        filmStorage.update(film);
    }

    public void unlike(int filmId, int userId) {
        checkId(filmId);
        checkUser(userId);
        Film film = filmStorage.getFilm(filmId);
        Set<Integer> likes = film.getLikes();
        likes.remove(userId);
        film.setLikes(likes);
        filmStorage.update(film);
    }

    public List<Film> popularFilms(long count) {
        if (count <= 0) {
            throw new ValidateException("Неверный параметр count");
        }
        if (filmStorage.findAll().isEmpty()) {
            return null;
        }
        return filmStorage.findAll().stream()
                .sorted()
                .limit(count)
                .toList();
    }

    private void checkId(int id) {
        List<Integer> allId = filmStorage.findAll().stream()
                .map(Film::getId)
                .toList();
        if (!allId.contains(id)) {
            throw new NotFoundException("Неизвестный фильм");
        }
    }

    private void checkUser(int userId) {
        List<Integer> allUserId = userStorage.findAll().stream()
                .map(User::getId)
                .toList();
        if (!allUserId.contains(userId)) {
            throw new NotFoundException("Неизвестный пользователь");
        }
    }
}
