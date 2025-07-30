package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserInputDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl service;

    private UserInputDto dto;

    @BeforeEach
    void setUp() {
        dto = UserInputDto.builder()
                .name("UserName")
                .email("Usermail@mail.com")
                .build();
    }

    @Test
    void create_whenEmailValid_thenSavedUser() {
        User entity = UserMapper.toEntity(dto);
        when(repository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(repository.save(any(User.class))).thenReturn(entity);

        UserResponseDto response = service.create(dto);

        assertThat(response, notNullValue());
        assertThat(response.getName(), equalTo(dto.getName()));
        assertThat(response.getEmail(), equalTo(dto.getEmail()));
        verify(repository).existsByEmail(dto.getEmail());
        verify(repository).save(any(User.class));
    }

    @Test
    void create_whenEmailNotValid_thenConflictExceptionThrown() {
        when(repository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.create(dto));
        verify(repository).existsByEmail(dto.getEmail());
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void update_whenUserFound_thenUpdateFields() {
        Long userId = 1L;
        User entity = UserMapper.toEntity(dto);
        entity.setId(userId);
        UserInputDto newDto = UserInputDto.builder()
                .name("NewNameUser")
                .email("NewUniqueMail@mail.com")
                .build();
        when(repository.findById(userId)).thenReturn(Optional.of(entity));
        when(repository.existsByEmail(newDto.getEmail())).thenReturn(false);

        UserResponseDto response = service.update(userId, newDto);

        assertThat(response.getName(), equalTo(entity.getName()));
        assertThat(response.getEmail(), equalTo(entity.getEmail()));
    }

    @Test
    void update_whenEmailNotValid_thenConflictExceptionThrown() {
        Long userId = 1L;
        User entity = UserMapper.toEntity(dto);
        entity.setId(userId);
        UserInputDto newDto = UserInputDto.builder()
                .name("NewNameUser")
                .email("ExistingMail@mail.com")
                .build();
        when(repository.findById(userId)).thenReturn(Optional.of(entity));
        when(repository.existsByEmail(newDto.getEmail())).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.update(userId, newDto));
        verify(repository).existsByEmail(newDto.getEmail());
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void update_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 1L;
        UserInputDto newDto = UserInputDto.builder()
                .name("NewNameUser")
                .email("ExistingMail@mail.com")
                .build();
        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.update(userId, newDto));
        verify(repository, never()).existsByEmail(newDto.getEmail());
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void getById_whenUserFound_thenReturnedUser() {
        Long userId = 1L;
        User entity = UserMapper.toEntity(dto);
        entity.setId(userId);
        when(repository.findById(userId)).thenReturn(Optional.of(entity));

        UserResponseDto response = service.getById(userId);

        assertThat(response.getName(), equalTo(entity.getName()));
        assertThat(response.getEmail(), equalTo(entity.getEmail()));
        verify(repository).findById(anyLong());
    }

    @Test
    void getById_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 1L;
        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getById(userId));
        verify(repository).findById(anyLong());
    }

    @Test
    void delete_whenUserFound_thenDeletedUser() {
        Long userId = 1L;
        User entity = UserMapper.toEntity(dto);
        entity.setId(userId);
        when(repository.findById(userId)).thenReturn(Optional.of(entity));

        service.delete(userId);

        verify(repository).findById(userId);
        verify(repository).delete(entity);
    }

    @Test
    void delete_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 1L;
        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.delete(userId));
        verify(repository).findById(userId);
        verify(repository, never()).delete(any(User.class));
    }
}
