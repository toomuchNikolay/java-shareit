package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.entity.User;

public interface UserService {
    UserDto create(UserCreateDto dto);

    UserDto update(Long userId, UserUpdateDto dto);

    UserDto getById(Long userId);

    User findByIdOrThrow(Long userId);

    void delete(Long userId);
}
