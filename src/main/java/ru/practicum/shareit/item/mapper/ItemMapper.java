package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {
    public static Item toEntity(ItemCreateDto dto, User owner) {
        return Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .owner(owner)
                .request(dto.getRequest() != null ? dto.getRequest() : null)
                .build();
    }

    public static ItemDto toDto(Item entity) {
        return ItemDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .available(entity.getAvailable())
                .ownerId(entity.getOwner().getId())
                .build();
    }

    public static ItemShortDto toShortDto(Item entity) {
        return ItemShortDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public static ItemFullDto toFullDto(Item entity, LocalDateTime lastDateBooking, LocalDateTime nextDateBooking, Collection<CommentDto> comments) {
        return ItemFullDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .available(entity.getAvailable())
                .ownerId(entity.getOwner().getId())
                .lastBooking(lastDateBooking)
                .nextBooking(nextDateBooking)
                .comments(comments)
                .build();
    }

    public static void updateFieldsItem(Item entity, ItemUpdateDto dto) {
        Optional.ofNullable(dto.getName()).ifPresent(entity::setName);
        Optional.ofNullable(dto.getDescription()).ifPresent(entity::setDescription);
        Optional.ofNullable(dto.getAvailable()).ifPresent(entity::setAvailable);
    }
}
