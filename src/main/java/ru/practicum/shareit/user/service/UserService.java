package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.entity.User;

@Transactional(readOnly = true)
public interface UserService {
    @Transactional
    UserDto create(UserCreateDto dto);

    @Transactional
    UserDto update(Long userId, UserUpdateDto dto);

    UserDto getById(Long userId);

    User findByIdOrThrow(Long userId);

    @Transactional
    void delete(Long userId);
}
