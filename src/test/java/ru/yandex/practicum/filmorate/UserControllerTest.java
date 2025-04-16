package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    UserController userController;
    User user;
    Validator validator;
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    @BeforeEach
    void beforeEach() {
        userController = new UserController();
        user = new User();
        user.setLogin("username");
        user.setEmail("username@email");
        user.setBirthday(LocalDate.parse("1996-02-27"));
        validator = factory.getValidator();
    }

    @Test
    void blankLoginTest() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
        user.setLogin(" ");
        Set<ConstraintViolation<User>> violations2 = validator.validate(user);
        assertFalse(violations2.isEmpty());
    }

    @Test
    void loginWithWhiteSpaceTest() {
        user.setLogin("user name");
        Exception e = assertThrows(ConditionsNotMetException.class, () -> userController.create(user));
        assertEquals("Нельзя добавлять пробелы в логин", e.getMessage());
    }

    @Test
    void badEmailTest() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
        user.setEmail("username");
        Set<ConstraintViolation<User>> violations2 = validator.validate(user);
        assertFalse(violations2.isEmpty());
    }

    @Test
    void blankEmailTest() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
        user.setEmail(" ");
        Set<ConstraintViolation<User>> violations2 = validator.validate(user);
        assertFalse(violations2.isEmpty());
    }

    @Test
    void birthdayBeforeNowTest() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
        user.setBirthday(LocalDate.now().plusYears(5));
        Set<ConstraintViolation<User>> violations2 = validator.validate(user);
        assertFalse(violations2.isEmpty());
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
