package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.exception.errors.ErrorMessage.HEADER_USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ResponseEntity<ItemResponseDto> create(@RequestHeader(HEADER_USER_ID) long userId,
                                                  @RequestBody ItemInputDto item) {
        log.info("Request create item by user id={}: {}", userId, item);
        ItemResponseDto responseDto = service.create(userId, item);
        log.info("Created item: {}", responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> update(@PathVariable Long itemId,
                                                  @RequestHeader(HEADER_USER_ID) long userId,
                                                  @RequestBody ItemInputDto item) {
        log.info("Request update item id={} by user id={}: {}", itemId, userId, item);
        ItemResponseDto responseDto = service.update(itemId, userId, item);
        log.info("Updated item: {}", responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDetailsDto> getById(@PathVariable Long itemId,
                                                          @RequestHeader(HEADER_USER_ID) long userId) {
        log.info("Request get item id={} by user id={}", itemId, userId);
        ItemResponseDetailsDto responseDetailsDto = service.getById(itemId, userId);
        log.info("Returned item: {}", responseDetailsDto);
        return ResponseEntity.ok(responseDetailsDto);
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getAllByUserId(@RequestHeader(HEADER_USER_ID) long userId,
                                                                @RequestParam(defaultValue = "0") int from,
                                                                @RequestParam(defaultValue = "10") int size) {
        log.info("Request get all own items by user id={}", userId);
        List<ItemResponseDto> responseDtos = service.getAllById(userId, from, size);
        log.info("Returned list of size {}", responseDtos.size());
        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemResponseDto>> search(@RequestHeader(HEADER_USER_ID) long userId,
                                                        @RequestParam String text,
                                                        @RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "10") int size) {
        log.info("Request search by text={}", text);
        List<ItemResponseDto> responseDtos = service.search(userId, text, from, size);
        log.info("Returned list of size {}", responseDtos.size());
        return ResponseEntity.ok(responseDtos);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponseDto> addComment(@PathVariable Long itemId,
                                                         @RequestHeader(HEADER_USER_ID) long userId,
                                                         @RequestBody CommentInputDto comment) {
        log.info("Request create comment to item id={} by user id={}: {}", itemId, userId, comment);
        CommentResponseDto responseDto = service.addComment(itemId, userId, comment);
        log.info("Created comment: {}", responseDto);
        return ResponseEntity.ok(responseDto);
    }
}
