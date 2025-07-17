package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import static ru.practicum.shareit.exception.errors.ErrorMessage.EMAIL_ALREADY_EXISTS;
import static ru.practicum.shareit.exception.errors.ErrorMessage.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    @Transactional
    public UserDto create(UserCreateDto dto) {
        validateEmail(dto.getEmail());
        User user = UserMapper.toEntity(dto);
        repository.save(user);
        log.info("Добавлена сущность User: {}", user);
        return UserMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto update(Long userId, UserUpdateDto dto) {
        User user = findByIdOrThrow(userId);
        UserMapper.updateFieldsUser(user, dto);
        log.info("Обновлена сущность User: {}", user);
        return UserMapper.toDto(user);
    }

    @Override
    public UserDto getById(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        log.info("Найдена сущность User: {}", user);
        return UserMapper.toDto(user);
    }

    @Override
    public User findByIdOrThrow(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        User user = findByIdOrThrow(userId);
        repository.delete(user);
        log.info("Удалена сущность User: {}", user);
    }

    private void validateEmail(String email) {
        if (repository.existsByEmail(email)) {
            log.warn("Попытка повторно зарегистрировать на почтовый адрес - {}", email);
            throw new ConflictException(EMAIL_ALREADY_EXISTS);
        }
    }
}
