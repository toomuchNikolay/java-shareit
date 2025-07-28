package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserInputDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService service;

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestBody UserInputDto user) {
        log.info("Request create user: {}", user);
        UserResponseDto responseDto = service.create(user);
        log.info("Created user: {}", responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponseDto> update(@PathVariable Long userId,
                                                  @RequestBody UserInputDto user) {
        log.info("Request update user id={}: {}", userId, user);
        UserResponseDto responseDto = service.update(userId, user);
        log.info("Updated user: {}", responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long userId) {
        log.info("Request get user id={}", userId);
        UserResponseDto responseDto = service.getById(userId);
        log.info("Returned user: {}", responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        log.info("Request delete user id={}", userId);
        service.delete(userId);
        log.info("User id={} deleted", userId);
        return ResponseEntity.noContent().build();
    }
}
