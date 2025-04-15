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
    void blankLoginTest() {
        user.setLogin(" ");
        Exception e = assertThrows(ValidateException.class, () -> userController.create(user));
        assertEquals("Логин должен быть прописан", e.getMessage());
    }

    @Test
    void loginWithWhiteSpaceTest() {
        user.setLogin("user name");
        Exception e =  assertThrows(ConditionsNotMetException.class, () -> userController.create(user));
        assertEquals("Нельзя добавлять пробелы в логин", e.getMessage());
    }

    @Test
    void badEmailTest() {
        user.setEmail("username");
        Exception e = assertThrows(ConditionsNotMetException.class, () -> userController.create(user));
        assertEquals(user.getEmail() + " - Это не имейл", e.getMessage());
    }

    @Test
    void blankEmailTest() {
        user.setEmail("  ");
        Exception e = assertThrows(ValidateException.class, () -> userController.create(user));
        assertEquals("Имейл должен быть записан", e.getMessage());
    }

    @Test
    void birthdayBeforeNowTest() {
        user.setBirthday(LocalDate.now().plusYears(5));
        Exception e = assertThrows(ValidateException.class, () -> userController.create(user));
        assertEquals("Не правильно указана дата рождения", e.getMessage());
    }

    @Test
    void createUserTest() {
        userController.create(user);
        assertEquals(1, userController.findAll().size(), "Ошибка создания пользователя");
        assertEquals(user, userController.findAll().getFirst());
    }

    @Test
    void updateUserTest() {
        User user1 = userController.create(user);
        user1.setLogin("username22");
        User userTest = userController.update(user1);
        assertTrue(userController.findAll().contains(userTest));
    }

    @Test
    void unknownUserUpdate() {
        User testUser = userController.create(user);
        testUser.setId(100);
        Exception e = assertThrows(NotFoundException.class, () -> userController.update(testUser));
        assertEquals("Такого пользователя не существует", e.getMessage());
    }

    @Test
    void nameEqualLoginTest() {
        userController.create(user);
        assertEquals(user.getLogin(), user.getName());
    }
}
