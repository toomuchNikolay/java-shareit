package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;

public interface ItemService {
    ItemDto addItem(Long userId, ItemCreateDto dto);

    ItemDto updateItem(Long itemId, Long userId, ItemUpdateDto dto);

    ItemDto getItemById(Long itemId);

    Collection<ItemDto> getAllItemByUser(Long userId);

    Collection<ItemDto> searchItems(String text);
}
