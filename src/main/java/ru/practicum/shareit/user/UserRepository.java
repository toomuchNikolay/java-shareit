package ru.practicum.shareit.user;

import java.util.Optional;

public interface UserRepository {
    User save(User user);

    User update(User user);

    Optional<User> findUserById(Long userId);

    void delete(Long id);

    boolean isUsedEmail(String email);
}
