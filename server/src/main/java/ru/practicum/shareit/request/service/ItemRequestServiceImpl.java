package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDetailsDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static ru.practicum.shareit.exception.errors.ErrorMessage.REQUEST_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserService userService;

    @Override
    @Transactional
    public ItemRequestResponseDto create(long requestorId, ItemRequestInputDto dto) {
        User requestor = userService.findUserOrThrow(requestorId);
        ItemRequest itemRequest = repository.save(ItemRequestMapper.toEntity(dto, requestor));
        log.info("Добавлена сущность ItemRequest: {}", itemRequest);
        return ItemRequestMapper.toDto(itemRequest);
    }

    @Override
    public List<ItemRequestResponseDetailsDto> getOwnItemRequests(long requestorId, int from, int size) {
        User requestor = userService.findUserOrThrow(requestorId);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<ItemRequest> result = repository.findAllByRequestorIdOrderByCreatedDesc(requestor.getId(), page).getContent();
        log.info("Возвращен список в размере {} сущностей ItemRequest пользователя id={}", result.size(), requestorId);
        return ItemRequestMapper.toDetailsDto(result);
    }

    @Override
    public List<ItemRequestResponseDto> getOthersItemRequests(long requestorId, int from, int size) {
        User requestor = userService.findUserOrThrow(requestorId);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<ItemRequest> result = repository.findAllByRequestorIdNotOrderByCreatedDesc(requestor.getId(), page).getContent();
        log.info("Возвращен список в размере {} сущностей ItemRequest всех пользователей, кроме id={}", result.size(), requestorId);
        return ItemRequestMapper.toDto(result);
    }

    @Override
    public ItemRequestResponseDetailsDto getById(Long requestId) {
        ItemRequest itemRequest = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(REQUEST_NOT_FOUND));
        log.info("Возвращена сущность ItemRequest: {}", itemRequest);
        return ItemRequestMapper.toDetailsDto(itemRequest);
    }

    @Override
    public ItemRequest findItemRequestOrThrow(Long requestId) {
        return repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(REQUEST_NOT_FOUND));
    }
}
