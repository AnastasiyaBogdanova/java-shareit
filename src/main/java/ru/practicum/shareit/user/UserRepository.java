package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long counter = 0;

    public User createUser(User user) {
        checkEmailUniqueness(user.getEmail());
        if (user.getId() == null) {
            user.setId(++counter);
        }
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(Long id, User newUser) {
        Optional<User> oldUser = findById(id);
        if (newUser.getName() != null) {
            oldUser.get().setName(newUser.getName());
        }
        if (newUser.getEmail() != null) {
            oldUser.get().setEmail(newUser.getEmail());
        }
        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
            checkEmailUniquenessForUpdate(newUser.getEmail(), id);
        }

        users.put(id, oldUser.get());
        return oldUser.get();
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public void deleteUser(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден");
        }
        users.remove(id);
    }

    private void checkEmailUniqueness(String email) {
        if (users.values().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email))) {
            throw new ConflictException("Email " + email + " уже используется");
        }
    }

    private void checkEmailUniquenessForUpdate(String email, Long userId) {
        users.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email) && !u.getId().equals(userId))
                .findFirst()
                .ifPresent(u -> {
                    throw new ConflictException("Email " + email + " уже используется");
                });
    }
}