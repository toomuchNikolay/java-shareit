package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserService service;

    @PostMapping
    public UserDto create(@RequestBody @Valid UserCreateDto user) {
        log.info("POST /users | user: {}", user);
        return service.create(user);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable @Positive Long userId, @RequestBody @Valid UserUpdateDto user) {
        log.info("PATCH /users/{} | user: {}", userId, user);
        return service.update(userId, user);
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable @Positive Long userId) {
        log.info("GET /users/{}", userId);
        return service.getById(userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long userId) {
        log.info("DELETE /users/{}", userId);
        service.delete(userId);
    }
}
