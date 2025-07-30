package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserInputDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.entity.User;

public interface UserService {
    UserResponseDto create(UserInputDto dto);

    UserResponseDto update(Long userId, UserInputDto dto);

    UserResponseDto getById(Long userId);

    void delete(Long userId);

    User findUserOrThrow(Long userId);
}
