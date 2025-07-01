package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    public List<User> findAll() {
        return users.values().stream().toList();
    }

    public User getUser(int id) {
        return users.get(id);
    }

    public User create(User user) {
        validate(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан новый пользователь");
        return user;
    }

    public User update(User user) {
        if (user == null) {
            log.warn("Объект пустой");
            return null;
        }

        if (user.getId() == null) {
            log.warn("Id должен быть указан");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (!users.containsKey(user.getId())) {
            log.warn("Такого пользователя не существует");
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

    public void checkUser(int userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Неизвестный пользователь");
        }
    }

    private void validate(User user) {

        if (user.getLogin().contains(" ")) {
            log.warn("Нельзя добавлять пробелы в логин");
            throw new ConditionsNotMetException("Нельзя добавлять пробелы в логин");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
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
