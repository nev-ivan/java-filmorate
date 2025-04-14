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
        if (user == null) {
            log.warn("Объект пустой");
            return null;
        }
        validate(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан новый пользователь");
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if (user == null) {
            log.warn("Объект пустой");
            return null;
        }

        if (user.getId() == null) {
            log.warn("ConditionsNotMetException");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (!users.containsKey(user.getId())) {
            log.warn("NotFoundException");
            throw new NotFoundException("Такого пользователя не существует");
        }

        User newUser = users.get(user.getId());

        if (user.getEmail() != null && user.getEmail().contains("@")) {
            newUser.setEmail(user.getEmail());
        }

        if (user.getLogin() != null && !user.getLogin().isBlank() && !user.getLogin().contains(" ")) {
            newUser.setLogin(user.getLogin());
        }

        if (user.getName() != null && !user.getName().isBlank()) {
            newUser.setName(user.getName());
        } else if (user.getName() != null && user.getName().isBlank() && newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }

        if (user.getBirthday() != null && user.getBirthday().isBefore(LocalDate.now())) {
            newUser.setBirthday(user.getBirthday());
        }

        users.put(user.getId(), user);
        log.info("Данные пользователя обновлены");
        return user;
    }

    private void validate(User user) {
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

        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
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
