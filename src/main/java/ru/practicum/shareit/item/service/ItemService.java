package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.entity.Item;

import java.util.Collection;

public interface ItemService {
    ItemDto create(Long ownerId, ItemCreateDto dto);

    ItemDto update(Long itemId, Long userId, ItemUpdateDto dto);

    ItemFullDto getById(Long itemId, Long userId);

    Item findByIdOrThrow(Long itemId);

    Collection<ItemDto> getAllById(Long ownerId);

    Collection<ItemDto> search(String text, int from, int size);

    CommentDto addComment(Long itemId, Long authorId, CommentCreateDto dto);

    boolean hasUserAnyItems(Long ownerId);
}
