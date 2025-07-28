package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.exception.errors.ErrorMessage.HEADER_USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService service;

    @PostMapping
    public ResponseEntity<BookingResponseDto> create(@RequestHeader(HEADER_USER_ID) long userId,
                                                     @RequestBody BookingInputDto booking) {
        log.info("Request create booking by user id={}: {}", userId, booking);
        BookingResponseDto responseDto = service.create(userId, booking);
        log.info("Created booking: {}", responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> approve(@PathVariable Long bookingId,
                                                      @RequestHeader(HEADER_USER_ID) long userId,
                                                      @RequestParam boolean approved) {
        log.info("Request by user id={} approve booking id={}?approved={}", userId, bookingId, approved);
        BookingResponseDto responseDto = service.approve(bookingId, userId, approved);
        log.info("Updated booking: {}", responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getById(@PathVariable Long bookingId,
                                                      @RequestHeader(HEADER_USER_ID) long userId) {
        log.info("Request get booking id={} by user id={}", bookingId, userId);
        BookingResponseDto responseDto = service.getById(bookingId, userId);
        log.info("Returned booking: {}", responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getBookingsByUser(@RequestHeader(HEADER_USER_ID) long userId,
                                                                      @RequestParam(defaultValue = "ALL") String state,
                                                                      @RequestParam(defaultValue = "0") int from,
                                                                      @RequestParam(defaultValue = "10") int size) {
        log.info("Request get bookings by user id={} with state={}", userId, state);
        List<BookingResponseDto> responseDtos = service.getBookingsByUser(userId, state, from, size);
        log.info("Returned list of size {}", responseDtos.size());
        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getBookingsByOwner(@RequestHeader(HEADER_USER_ID) long userId,
                                                                       @RequestParam(defaultValue = "ALL") String state,
                                                                       @RequestParam(defaultValue = "0") int from,
                                                                       @RequestParam(defaultValue = "10") int size) {
        log.info("Request get bookings by owner id={} with state={}", userId, state);
        List<BookingResponseDto> responseDtos = service.getBookingsByOwner(userId, state, from, size);
        log.info("Returned list of size {}", responseDtos.size());
        return ResponseEntity.ok(responseDtos);
    }
}
