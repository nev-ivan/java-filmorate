package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public void makeFriends(int user1id, int user2id) {
        userStorage.checkUser(user1id);
        userStorage.checkUser(user2id);
        if (userStorage.getUser(user1id).getFriends().contains(user2id)) {
            log.warn("Пользователь уже в друзьях");
            throw new ConditionsNotMetException("Пользователь уже в друзьях");
        }
        userStorage.getUser(user1id).getFriends().add(user2id);
        userStorage.getUser(user2id).getFriends().add(user1id);
        log.info("Добавление в друзья успешно");
    }

    public void deleteFromFriends(int user1id, int user2id) {
        userStorage.checkUser(user1id);
        userStorage.checkUser(user2id);
        userStorage.getUser(user1id).getFriends().remove(user2id);
        userStorage.getUser(user2id).getFriends().remove(user1id);
    }

    public List<User> mutualFriends(int user1id, int user2id) {
        userStorage.checkUser(user1id);
        userStorage.checkUser(user2id);
        User user1 = userStorage.getUser(user1id);
        User user2 = userStorage.getUser(user2id);
        return user1.getFriends().stream()
                .filter(friendId -> user2.getFriends().contains(friendId))
                .map(userStorage::getUser)
                .toList();
    }

    public List<User> getFriends(int userId) {
        userStorage.checkUser(userId);
        return userStorage.getUser(userId).getFriends().stream()
                .map(userStorage::getUser)
                .toList();
    }
}
