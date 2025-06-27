package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemService service;

    @PostMapping
    public ItemDto addItem(@RequestHeader(HEADER_USER_ID) Long userId, @RequestBody @Valid ItemCreateDto item) {
        log.info("POST /items | userId = {} | item: {}", userId, item);
        return service.addItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId, @RequestHeader(HEADER_USER_ID) Long userId,
                              @RequestBody @Valid ItemUpdateDto item) {
        log.info("PATCH /items/{} | userId = {} | item: {}", itemId, userId, item);
        return service.updateItem(itemId, userId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        log.info("GET /items/{}", itemId);
        return service.getItemById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getAllItemsByUser(@RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("GET /items | userId = {}",userId);
        return service.getAllItemByUser(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text) {
        log.info("GET /items/search | text = {}", text);
        return service.searchItems(text);
    }
}
