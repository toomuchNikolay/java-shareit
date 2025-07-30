package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.entity.Item;

import java.util.List;

public interface ItemService {
    ItemResponseDto create(long ownerId, ItemInputDto dto);

    ItemResponseDto update(Long itemId, long userId, ItemInputDto dto);

    ItemResponseDetailsDto getById(Long itemId, long userId);

    List<ItemResponseDto> getAllById(long ownerId, int from, int size);

    List<ItemResponseDto> search(long userId, String text, int from, int size);

    Item findItemOrThrow(Long itemId);

    CommentResponseDto addComment(Long itemId, long authorId, CommentInputDto dto);

    boolean hasUserAnyItems(long ownerId);
}
