package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

public interface UserService {
    UserDto addUser(UserCreateDto dto);

    UserDto updateUser(Long userId, UserUpdateDto dto);

    UserDto getUserById(Long userId);

    void deleteUser(Long userId);
}
