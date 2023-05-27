package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int userId, int friendId) {
        userStorage.findUser(userId).setFriend(friendId);
        userStorage.findUser(friendId).setFriend(userId);
    }

    public List<User> getAllFriend(int userId) {
        try {

            Set<Integer> friendIds = userStorage.findUser(userId).getFriends();
            List<User> friends = new ArrayList<>();
            for (int friendId : friendIds) {
                User friend = userStorage.findUser(friendId);
                if (friend != null) {
                    friends.add(friend);
                }
            }
            return friends;
        } catch (NotFoundException e) {
            return Collections.emptyList(); // Возвращаем пустой список при исключении 404
        }
    }

    public void delFriend(int userId, int friendId) {
        userStorage.findUser(userId).delFriend(friendId);
        userStorage.findUser(friendId).delFriend(userId);
    }

    public Collection<User> getCommonFriends(int userId, int friendId) {

        return userStorage.findCommonFriends(userId, friendId);
    }
}
