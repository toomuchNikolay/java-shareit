package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.AddItemRequestRequest;
import ru.practicum.shareit.request.service.ItemRequestClient;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestGatewayController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    private final ItemRequestClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER_USER_ID) @Positive long userId,
                                         @RequestBody @Valid AddItemRequestRequest request) {
        log.info(">> POST /requests | userId = {}", userId);
        ResponseEntity<Object> response = client.create(userId, request);
        log.info("<< POST /requests | userId = {} | status: {}", userId, response.getStatusCode());
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getOwn(@RequestHeader(HEADER_USER_ID) @Positive long userId,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        log.info(">> GET /requests | userId = {}", userId);
        ResponseEntity<Object> response = client.getOwn(userId, from, size);
        log.info("<< GET /requests | userId = {} | status: {}", userId, response.getStatusCode());
        return response;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOthers(@RequestHeader(HEADER_USER_ID) @Positive long userId,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "10") int size) {
        log.info(">> GET /requests/all | userId = {}", userId);
        ResponseEntity<Object> response = client.getOthers(userId, from, size);
        log.info("<< GET /requests/all | userId = {} | status: {}", userId, response.getStatusCode());
        return response;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> get(@PathVariable @Positive Long requestId,
                                      @RequestHeader(HEADER_USER_ID) @Positive long userId) {
        log.info(">> GET /requests/{} | userId = {}", requestId, userId);
        ResponseEntity<Object> response = client.get(requestId, userId);
        log.info("<< GET /requests/{} | userId = {} | status: {}", requestId, userId, response.getStatusCode());
        return response;
    }
}
