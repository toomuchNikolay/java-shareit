package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.exception.DuplicatedEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final Set<String> usedEmail = new HashSet<>();
    private final UserRepository repository;

    @Override
    public UserDto addUser(UserCreateDto dto) {
        if (usedEmail.contains(dto.getEmail().toLowerCase().trim())) {
            log.warn("Попытка повторно зарегистрировать на почтовый адрес - {}", dto.getEmail().toLowerCase().trim());
            throw new DuplicatedEmailException("Указанный почтовый адрес уже зарегистрирован");
        }
        User added = UserMapper.toEntity(dto);
        added = repository.save(added);
        log.info("Добавлена сущность User: {}", added);
        usedEmail.add(added.getEmail().toLowerCase().trim());
        return UserMapper.toDto(added);
    }

    @Override
    public UserDto updateUser(Long userId, UserUpdateDto dto) {
        User findUser = repository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        if (dto.hasEmail() && usedEmail.contains(dto.getEmail().toLowerCase().trim())) {
            log.warn("Попытка повторно зарегистрировать на почтовый адрес - {}", dto.getEmail().toLowerCase().trim());
            throw new DuplicatedEmailException("Указанный почтовый адрес уже зарегистрирован");
        }
        User updated = UserMapper.updateFieldsUser(findUser, dto);
        updated = repository.update(updated);
        log.info("Обновлена сущность User: {}", updated);
        usedEmail.remove(findUser.getEmail().toLowerCase().trim());
        usedEmail.add(updated.getEmail().toLowerCase().trim());
        return UserMapper.toDto(updated);
    }

    @Override
    public UserDto getUserById(Long userId) {
        return repository.findUserById(userId)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    @Override
    public void deleteUser(Long userId) {
        repository.delete(userId);
        log.info("Удалена сущность User c id = {}", userId);
    }
}
