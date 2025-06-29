package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public void makeFriends(int user1id, int user2id) {
        checkId(user1id);
        checkId(user2id);
        log.info("Инициализация id пройдена");
        User user1 = userStorage.getUser(user1id);
        User user2 = userStorage.getUser(user2id);
        Set<Integer> friends1 = user1.getFriends();
        Set<Integer> friends2 = user2.getFriends();
        if (user1.getFriends().contains(user2id)) {
            log.warn("Пользователь уже в друзьях");
            throw new ConditionsNotMetException("Пользователь уже в друзьях");
        }
        friends1.add(user2.getId());
        friends2.add(user1.getId());
        user1.setFriends(friends1);
        user2.setFriends(friends2);
        userStorage.update(user1);
        userStorage.update(user2);
        log.info("Добавление в друзья успешно");
    }

    public void deleteFromFriends(int user1id, int user2id) {
        checkId(user1id);
        checkId(user2id);
        log.info("Инициализация id пройдена");
        User user1 = userStorage.getUser(user1id);
        User user2 = userStorage.getUser(user2id);
        Set<Integer> friends1 = user1.getFriends();
        Set<Integer> friends2 = user2.getFriends();
        friends1.remove(user2id);
        friends2.remove(user1id);
        user1.setFriends(friends1);
        user2.setFriends(friends2);
        userStorage.update(user1);
        userStorage.update(user2);
    }

    public List<User> mutualFriends(int user1id, int user2id) {
        checkId(user1id);
        checkId(user2id);
        log.info("Инициализация id пройдена");
        User user1 = userStorage.getUser(user1id);
        User user2 = userStorage.getUser(user2id);
        return user1.getFriends().stream()
                .filter(friendId -> user2.getFriends().contains(friendId))
                .map(userStorage::getUser)
                .toList();
    }

    public List<User> getFriends(int userId) {
        checkId(userId);
        User user = userStorage.getUser(userId);
        return user.getFriends().stream()
                .map(userStorage::getUser)
                .toList();
    }

    private void checkId(int id) {
        List<Integer> allId = userStorage.findAll().stream()
                .map(User::getId)
                .toList();
        if (!allId.contains(id)) {
            throw new NotFoundException("Неизвестный пользователь");
        }
    }
}
