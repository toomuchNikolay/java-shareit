package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserRepositoryInMemory implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> usedEmail = new HashSet<>();

    @Override
    public User save(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        usedEmail.add(user.getEmail().toLowerCase().trim());
        return user;
    }

    @Override
    public User update(User user) {
        users.replace(user.getId(), user);
        User old = users.get(user.getId());
        if (old.getEmail().equalsIgnoreCase(user.getEmail())) {
            usedEmail.remove(old.getEmail().toLowerCase().trim());
            usedEmail.add(user.getEmail().toLowerCase().trim());
        }
        return user;
    }

    @Override
    public Optional<User> findUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    @Override
    public boolean isUsedEmail(String email) {
        return usedEmail.contains(email.toLowerCase().trim());
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
