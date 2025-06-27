package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.exception.DuplicatedEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private static final String USER_NOT_FOUND = "Пользователь не найден";
    private final UserRepository repository;

    @Override
    public UserDto addUser(UserCreateDto dto) {
        checkEmail(dto.getEmail());
        User added = UserMapper.toEntity(dto);
        added = repository.save(added);
        log.info("Добавлена сущность User: {}", added);
        return UserMapper.toDto(added);
    }

    @Override
    public UserDto updateUser(Long userId, UserUpdateDto dto) {
        User findUser = repository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        if (dto.hasEmail() && !findUser.getEmail().equalsIgnoreCase(dto.getEmail())) {
            checkEmail(dto.getEmail());
        }
        User updated = UserMapper.updateFieldsUser(findUser, dto);
        updated = repository.update(updated);
        log.info("Обновлена сущность User: {}", updated);
        return UserMapper.toDto(updated);
    }

    @Override
    public UserDto getUserById(Long userId) {
        return repository.findUserById(userId)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
    }

    @Override
    public void deleteUser(Long userId) {
        User deleted = repository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        repository.delete(userId);
        log.info("Удалена сущность User: {}", deleted);
    }

    private void checkEmail(String email) {
        if (repository.isUsedEmail(email)) {
            log.warn("Попытка повторно зарегистрировать на почтовый адрес - {}", email.toLowerCase().trim());
            throw new DuplicatedEmailException("Указанный почтовый адрес уже зарегистрирован");
        }
    }
}
