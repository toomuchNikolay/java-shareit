package ru.practicum.shareit.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.entity.User;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {
    public static User toEntity(UserCreateDto dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public static UserDto toDto(User entity) {
        return UserDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .build();
    }

    public static UserShortDto toShortDto(User entity) {
        return UserShortDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public static void updateFieldsUser(User entity, UserUpdateDto dto) {
        Optional.ofNullable(dto.getName()).ifPresent(entity::setName);
        Optional.ofNullable(dto.getEmail()).ifPresent(entity::setEmail);
    }
}
