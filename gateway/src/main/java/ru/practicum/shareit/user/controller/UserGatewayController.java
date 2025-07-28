package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.AddUserRequest;
import ru.practicum.shareit.user.dto.ModifyUserRequest;
import ru.practicum.shareit.user.service.UserClient;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserGatewayController {
    private final UserClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid AddUserRequest request) {
        log.info(">> POST /users");
        ResponseEntity<Object> response = client.create(request);
        log.info("<< POST /users | status: {}", response.getStatusCode());
        return response;
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable @Positive Long userId,
                                         @RequestBody @Valid ModifyUserRequest request) {
        log.info(">> PATCH /users/{}", userId);
        ResponseEntity<Object> response = client.update(userId, request);
        log.info("<< PATCH /users/{} | status: {}", userId, response.getStatusCode());
        return response;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable @Positive Long userId) {
        log.info(">> GET /users/{}", userId);
        ResponseEntity<Object> response = client.get(userId);
        log.info("<< GET /users/{} | status: {}", userId, response.getStatusCode());
        return response;
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable @Positive Long userId) {
        log.info(">> DELETE /users/{}", userId);
        ResponseEntity<Object> response = client.delete(userId);
        log.info("<< DELETE /users/{} | status: {}", userId, response.getStatusCode());
        return response;
    }
}
