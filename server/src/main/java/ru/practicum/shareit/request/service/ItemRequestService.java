package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDetailsDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.entity.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestResponseDto create(long requestorId, ItemRequestInputDto dto);

    List<ItemRequestResponseDetailsDto> getOwnItemRequests(long requestorId, int from, int size);

    List<ItemRequestResponseDto> getOthersItemRequests(long requestorId, int from, int size);

    ItemRequestResponseDetailsDto getById(Long requestId);

    ItemRequest findItemRequestOrThrow(Long requestId);
}
