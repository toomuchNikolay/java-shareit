package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserInputDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

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
    public UserResponseDto create(UserInputDto dto) {
        validateEmail(dto.getEmail());
        User user = repository.save(UserMapper.toEntity(dto));
        log.info("Добавлена сущность User: {}", user);
        return UserMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto update(Long userId, UserInputDto dto) {
        User user = findUserOrThrow(userId);
        updateFields(user, dto);
        log.info("Обновлена сущность User: {}", user);
        return UserMapper.toDto(user);
    }

    @Override
    public UserResponseDto getById(Long userId) {
        User user = findUserOrThrow(userId);
        log.info("Возвращена сущность User: {}", user);
        return UserMapper.toDto(user);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        User user = findUserOrThrow(userId);
        repository.delete(user);
        log.info("Удалена сущность User: {}", user);
    }

    @Override
    public User findUserOrThrow(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    private void validateEmail(String email) {
        if (repository.existsByEmail(email)) {
            log.warn("Попытка повторно зарегистрировать на почтовый адрес - {}", email);
            throw new ConflictException(EMAIL_ALREADY_EXISTS);
        }
    }

    private void updateFields(User entity, UserInputDto dto) {
        Optional.ofNullable(dto.getName()).ifPresent(entity::setName);
        Optional.ofNullable(dto.getEmail()).ifPresent(email -> {
            if (!email.equals(entity.getEmail())) {
                validateEmail(email);
                entity.setEmail(email);
            }
        });
    }
}
