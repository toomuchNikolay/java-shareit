package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {
    public static Item toEntity(ItemCreateDto dto) {
        return Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .request(dto.getRequest())
                .build();
    }

    public static ItemDto toDto(Item entity) {
        return ItemDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .available(entity.getAvailable())
                .build();
    }

    public static Item updateFieldsItem(Item item, ItemUpdateDto dto) {
        if (dto.hasName()) {
            item.setName(dto.getName());
        }
        if (dto.hasDescription()) {
            item.setDescription(dto.getDescription());
        }
        if (dto.hasAvailable()) {
            item.setAvailable(dto.getAvailable());
        }
        return item;
    }
}
