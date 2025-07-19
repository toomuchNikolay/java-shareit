package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

import static ru.practicum.shareit.exception.errors.ErrorMessage.HEADER_USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingDto create(@RequestHeader(HEADER_USER_ID) @Positive Long userId,
                             @RequestBody @Valid BookingCreateDto booking) {
        log.info("POST /bookings | userId = {} | booking: {}", userId, booking);
        return service.create(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approvedBooking(@PathVariable @Positive Long bookingId,
                                      @RequestHeader(HEADER_USER_ID) @Positive Long userId,
                                      @RequestParam boolean approved) {
        log.info("PATCH /bookings/{} | userId = {} | approved = {}", bookingId, userId, approved);
        return service.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable @Positive Long bookingId,
                              @RequestHeader(HEADER_USER_ID) @Positive Long userId) {
        log.info("GET /bookings/{} | userId = {}", bookingId, userId);
        return service.getById(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> getBookingsByUser(@RequestHeader(HEADER_USER_ID) @Positive Long userId,
                                                    @RequestParam(defaultValue = "ALL") String state,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "10") int size) {
        log.info("GET /bookings/ | userId = {} | state = {}", userId, state);
        return service.getBookingsByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getBookingsByOwner(@RequestHeader(HEADER_USER_ID) @Positive Long userId,
                                                     @RequestParam(defaultValue = "ALL") String state,
                                                     @RequestParam(defaultValue = "0") int from,
                                                     @RequestParam(defaultValue = "10") int size) {
        log.info("GET /bookings/owner | userId = {} | state = {}", userId, state);
        return service.getBookingsByOwner(userId, state, from, size);
    }
}
