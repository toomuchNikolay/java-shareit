package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.AddCommentRequest;
import ru.practicum.shareit.item.dto.AddItemRequest;
import ru.practicum.shareit.item.dto.ModifyItemRequest;
import ru.practicum.shareit.item.service.ItemClient;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemGatewayController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER_USER_ID) @Positive long userId,
                                         @RequestBody @Valid AddItemRequest request) {
        log.info(">> POST /items | userId = {}", userId);
        ResponseEntity<Object> response = client.create(userId, request);
        log.info("<< POST /items | userId = {} | status: {}", userId, response.getStatusCode());
        return response;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable @Positive Long itemId,
                                         @RequestHeader(HEADER_USER_ID) @Positive long userId,
                                         @RequestBody @Valid ModifyItemRequest request) {
        log.info(">> PATCH /items/{} | userId = {}", itemId, userId);
        ResponseEntity<Object> response = client.update(itemId, userId, request);
        log.info("<< PATCH /items/{} | userId = {} | status: {}", itemId, userId, response.getStatusCode());
        return response;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@PathVariable @Positive Long itemId,
                                      @RequestHeader(HEADER_USER_ID) @Positive long userId) {
        log.info(">> GET /items/{} | userId = {}", itemId, userId);
        ResponseEntity<Object> response = client.get(itemId, userId);
        log.info("<< GET /items/{} | userId = {} | status: {}", itemId, userId, response.getStatusCode());
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(HEADER_USER_ID) @Positive long userId) {
        log.info(">> GET /items | userId = {}", userId);
        ResponseEntity<Object> response = client.getAll(userId);
        log.info("<< GET /items | userId = {} | status: {}", userId, response.getStatusCode());
        return response;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(HEADER_USER_ID) @Positive long userId,
                                         @RequestParam String text,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        log.info(">> GET /items/search | text = {}", text);
        ResponseEntity<Object> response = client.search(userId, text, from, size);
        log.info("<< GET /items/search | text = {} | status: {}", text, response.getStatusCode());
        return response;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable @Positive Long itemId,
                                             @RequestHeader(HEADER_USER_ID) @Positive long userId,
                                             @RequestBody @Valid AddCommentRequest request) {
        log.info(">> POST /items/{}/comment | userId = {}", itemId, userId);
        ResponseEntity<Object> response = client.addComment(itemId, userId, request);
        log.info("<< POST /items/{}/comment | userId = {} | status: {}", itemId, userId, response.getStatusCode());
        return response;
    }
}
