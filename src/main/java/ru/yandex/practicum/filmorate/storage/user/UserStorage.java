package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public interface UserStorage {
    public List<User> findAll();

    public User create(User user);

    public User update(User user);

    public User getUser(int id);

    public void checkUser(int id);
}
