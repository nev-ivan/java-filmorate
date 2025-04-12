package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    UserController userController;
    User user;

    @BeforeEach
    void beforeEach() {
        userController = new UserController();
        user = new User();
        user.setLogin("username");
        user.setEmail("username@email");
        user.setBirthday(LocalDate.parse("1996-02-27"));
    }

    @Test
    void createUserTest() {
        user.setLogin(" ");
        Exception e = assertThrows(ValidateException.class, () -> userController.create(user));
        assertEquals("Логин должен быть прописан", e.getMessage());
        user.setLogin("username");
        user.setEmail("username");
        e = assertThrows(ConditionsNotMetException.class, () -> userController.create(user));
        assertEquals(user.getEmail() + " - Это не имейл", e.getMessage());
        user.setEmail("username@email");
        user.setBirthday(LocalDate.now().plusYears(5));
        e = assertThrows(ValidateException.class, () -> userController.create(user));
        assertEquals("Не правильно указана дата рождения", e.getMessage());
    }

    @Test
    void nameEqualLoginTest() {
        userController.create(user);
        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    void updateUserTest() {
        User user1 = userController.create(user);
        user1.setLogin("user name");
        Exception e = assertThrows(ConditionsNotMetException.class, () -> userController.update(user1));
        assertEquals("Нельзя добавлять пробелы в логин", e.getMessage());
        user1.setLogin("username");
        user1.setId(185);
        e = assertThrows(NotFoundException.class, () -> userController.update(user1));
        assertEquals("Такого пользователя не существует", e.getMessage());
    }
}
