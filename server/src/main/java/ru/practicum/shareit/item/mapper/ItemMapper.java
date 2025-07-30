package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemInputDto;
import ru.practicum.shareit.item.dto.ItemResponseDetailsDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {
    public static Item toEntity(ItemInputDto dto, User owner, ItemRequest itemRequest) {
        return Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .owner(owner)
                .request(itemRequest)
                .build();
    }

    public static ItemResponseDto toDto(Item entity) {
        return ItemResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .available(entity.getAvailable())
                .ownerId(entity.getOwner().getId())
                .requestId(entity.getRequest() != null ? entity.getRequest().getId() : null)
                .build();
    }

    public static List<ItemResponseDto> toDto(List<Item> entities) {
        return entities.stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    public static ItemResponseDetailsDto toDetailsDto(Item entity, LocalDateTime last, LocalDateTime next) {
        return ItemResponseDetailsDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .available(entity.getAvailable())
                .ownerId(entity.getOwner().getId())
                .requestId(entity.getRequest() != null ? entity.getRequest().getId() : null)
                .lastBooking(last)
                .nextBooking(next)
                .comments(new ArrayList<>(entity.getComments().stream()
                        .map(CommentMapper::toDto)
                        .toList()
                ))
                .build();
    }
}
