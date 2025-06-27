package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemRepositoryInMemory implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item save(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.replace(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Collection<Item> findAllItemByUser(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .toList();
    }

    @Override
    public Collection<Item> searchItems(String text) {
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(item -> item.getAvailable().equals(Boolean.TRUE))
                .toList();
    }

    private long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
