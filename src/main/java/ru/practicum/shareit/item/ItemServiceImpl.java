package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.exception.AccessDeniedException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserService userService;

    @Override
    public ItemDto addItem(Long userId, ItemCreateDto dto) {
        UserDto owner = userService.getUserById(userId);
        Item added = ItemMapper.toEntity(dto);
        added.setOwner(UserMapper.toEntity(owner));
        added = repository.save(added);
        log.info("Добавлена сущность Item: {}", added);
        return ItemMapper.toDto(added);
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemUpdateDto dto) {
        Item findItem = repository.findItemById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
        UserDto owner = userService.getUserById(userId);
        if (!isOwnerItem(findItem, owner)) {
            log.warn("Отказано в доступе пользователю userId = {} при обновлении сущности itemId = {}", userId, itemId);
            throw new AccessDeniedException("Редактировать данные может только владелец вещи");
        }
        Item updated = ItemMapper.updateFieldsItem(findItem, dto);
        updated = repository.update(updated);
        log.info("Обновлена сущность Item: {}", updated);
        return ItemMapper.toDto(updated);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return repository.findItemById(itemId)
                .map(ItemMapper::toDto)
                .orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
    }

    @Override
    public Collection<ItemDto> getAllItemByUser(Long userId) {
        return repository.findAllItemByUser(userId).stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return repository.searchItems(text).stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    private boolean isOwnerItem(Item item, UserDto owner) {
        return item.getOwner().equals(UserMapper.toEntity(owner));
    }
}
