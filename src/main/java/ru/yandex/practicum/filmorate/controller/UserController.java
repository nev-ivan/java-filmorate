package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> findAll() {
        return users.values().stream().toList();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validate(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан новый пользователь");
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {

        if (user.getId() == null) {
            log.warn("ConditionsNotMetException");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        User newUser = users.get(user.getId());
        if (newUser == null) {
            log.warn("NotFoundException");
            throw new NotFoundException("Такого пользователя не существует");
        }

        if (user.getEmail() != null) {
            newUser.setEmail(user.getEmail());
            log.trace("Имейл заменен");
        }

        if (user.getLogin() != null) {
            newUser.setLogin(user.getLogin());
            log.trace("Логин заменен");
        }

        if (user.getName() != null) {
            newUser.setName(user.getName());
            log.trace("Имя заменено");
        }

        newUser.setBirthday(user.getBirthday());
        log.trace("Дата рождения заменена");

        validate(newUser);
        users.put(newUser.getId(), newUser);
        log.info("Данные пользователя обновлены");
        return newUser;
    }

    public void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("ValidationException");
            throw new ValidateException("Имейл должен быть записан");
        } else if (!user.getEmail().contains("@")) {
            log.warn("ConditionsNotMetException");
            throw new ConditionsNotMetException(user.getEmail() + " - Это не имейл");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("ValidationException");
            throw new ValidateException("Логин должен быть прописан");
        }
        if (user.getLogin().contains(" ")) {
            log.warn("ConditionsNotMetException");
            throw new ConditionsNotMetException("Нельзя добавлять пробелы в логин");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("ValidationException");
            throw new ValidateException("Не правильно указана дата рождения");
        }
    }

    private Integer getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
