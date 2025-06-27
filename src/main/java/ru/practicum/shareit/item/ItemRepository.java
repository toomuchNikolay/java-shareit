package ru.practicum.shareit.item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

    Item update(Item item);

    Optional<Item> findItemById(Long itemId);

    Collection<Item> findAllItemByUser(Long userId);

    Collection<Item> searchItems(String text);
}
