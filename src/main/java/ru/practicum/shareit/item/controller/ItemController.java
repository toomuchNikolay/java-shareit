package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

import static ru.practicum.shareit.exception.errors.ErrorMessage.HEADER_USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto create(@RequestHeader(HEADER_USER_ID) @Positive Long userId,
                          @RequestBody @Valid ItemCreateDto item) {
        log.info("POST /items | userId = {} | item: {}", userId, item);
        return service.create(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable @Positive Long itemId,
                          @RequestHeader(HEADER_USER_ID) @Positive Long userId,
                          @RequestBody @Valid ItemUpdateDto item) {
        log.info("PATCH /items/{} | userId = {} | item: {}", itemId, userId, item);
        return service.update(itemId, userId, item);
    }

    @GetMapping("/{itemId}")
    public ItemFullDto getById(@PathVariable @Positive Long itemId,
                               @RequestHeader(HEADER_USER_ID) @Positive Long userId) {
        log.info("GET /items/{} | userId = {}", itemId, userId);
        return service.getById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemDto> getAllById(@RequestHeader(HEADER_USER_ID) @Positive Long userId) {
        log.info("GET /items | userId = {}", userId);
        return service.getAllById(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text,
                                      @RequestParam(defaultValue = "0") int from,
                                      @RequestParam(defaultValue = "10") int size) {
        log.info("GET /items/search | text = {}", text);
        return service.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable @Positive Long itemId,
                                 @RequestHeader(HEADER_USER_ID) @Positive Long userId,
                                 @RequestBody @Valid CommentCreateDto comment) {
        log.info("POST /items/{}/comment | userId = {}", itemId, userId);
        return service.addComment(itemId, userId, comment);
    }
}
