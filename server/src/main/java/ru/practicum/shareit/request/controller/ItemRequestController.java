package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDetailsDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.exception.errors.ErrorMessage.HEADER_USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ResponseEntity<ItemRequestResponseDto> create(@RequestHeader(HEADER_USER_ID) long userId,
                                                         @RequestBody ItemRequestInputDto itemRequest) {
        log.info("Request create itemRequest user id={}: {}", userId, itemRequest);
        ItemRequestResponseDto responseDto = service.create(userId, itemRequest);
        log.info("Created itemRequest: {}", responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestResponseDetailsDto>> getOwn(@RequestHeader(HEADER_USER_ID) long userId,
                                                                      @RequestParam(defaultValue = "0") int from,
                                                                      @RequestParam(defaultValue = "10") int size) {
        log.info("Request get own itemRequests by user id={}", userId);
        List<ItemRequestResponseDetailsDto> responseDetailsDtos = service.getOwnItemRequests(userId, from, size);
        log.info("Returned list of size {}", responseDetailsDtos.size());
        return ResponseEntity.ok(responseDetailsDtos);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestResponseDto>> getOthers(@RequestHeader(HEADER_USER_ID) long userId,
                                                                  @RequestParam(defaultValue = "0") int from,
                                                                  @RequestParam(defaultValue = "10") int size) {
        log.info("Request get others itemRequests by user id={}", userId);
        List<ItemRequestResponseDto> responseDtos = service.getOthersItemRequests(userId, from, size);
        log.info("Returned list of size {}", responseDtos.size());
        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestResponseDetailsDto> getById(@PathVariable Long requestId,
                                                                 @RequestHeader(HEADER_USER_ID) long userId) {
        log.info("Request get itemRequest id={} by user id={}", requestId, userId);
        ItemRequestResponseDetailsDto responseDetailsDto = service.getById(requestId);
        log.info("Returned itemRequest: {}", responseDetailsDto);
        return ResponseEntity.ok(responseDetailsDto);
    }
}
