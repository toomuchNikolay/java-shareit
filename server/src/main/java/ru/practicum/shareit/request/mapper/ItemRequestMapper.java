package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDetailsDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemRequestMapper {
    public static ItemRequest toEntity(ItemRequestInputDto dto, User requestor) {
        return ItemRequest.builder()
                .description(dto.getDescription())
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
    }

    public static ItemRequestResponseDto toDto(ItemRequest entity) {
        return ItemRequestResponseDto.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .created(entity.getCreated())
                .build();
    }

    public static List<ItemRequestResponseDto> toDto(List<ItemRequest> entities) {
        return entities.stream()
                .map(ItemRequestMapper::toDto)
                .toList();
    }

    public static ItemRequestResponseDetailsDto toDetailsDto(ItemRequest entity) {
        return ItemRequestResponseDetailsDto.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .created(entity.getCreated())
                .items(new ArrayList<>(entity.getItems().stream()
                        .map(ItemMapper::toDto)
                        .toList()
                ))
                .build();
    }

    public static List<ItemRequestResponseDetailsDto> toDetailsDto(List<ItemRequest> entities) {
        return entities.stream()
                .map(ItemRequestMapper::toDetailsDto)
                .toList();
    }
}
