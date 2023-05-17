package ru.yandex.practicum.filmorate.storage.inMemory;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

@RestController
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private static int nextId = 0;


    @Override
    @GetMapping("/users")
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        if (users.remove(id) != null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        validate(user);
        int id = ++nextId;
        User newUser = new User(user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), id);
        users.put(newUser.getId(), newUser);
        return ResponseEntity.ok(newUser);
    }

    @Override
    @PutMapping("/users")
    public User updateUser(@RequestBody User userToUpdate) {
        User existingUser = users.get(userToUpdate.getId());
        if (existingUser == null) {
            throw new InternalServerException("User with id " + userToUpdate.getId() + " not found");
        }
        validate(userToUpdate);

        existingUser.setEmail(userToUpdate.getEmail());
        existingUser.setBirthday(userToUpdate.getBirthday());
        existingUser.setLogin(userToUpdate.getLogin());
        existingUser.setName(userToUpdate.getName());


        return existingUser;
    }

    @Override
    @GetMapping("/users/{id}")
    public User findUserById(@PathVariable int id) {
        if (users.get(id) != null) {
            return users.get(id);
        } else {
            throw new NoSuchElementException();
        }
    }

    public void validate(User user) {
        String email = user.getEmail();
        String login = user.getLogin();
        LocalDate birthday = user.getBirthday();

        if (email.trim().isEmpty() || !email.contains("@")) {
            throw new ValidationException("Email must contain @ symbol");
        }
        if (login.trim().isEmpty() || login.contains(" ")) {
            throw new ValidationException("Login must not contain spaces");
        }
        if (birthday.isAfter(LocalDate.now())) {
            throw new ValidationException("Date of birth cannot be in the future");
        }
    }
}
