package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.AddBookingRequest;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.service.BookingClient;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingGatewayController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    private final BookingClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER_USER_ID) @Positive long userId,
                                         @RequestBody @Valid AddBookingRequest request) {
        log.info(">> POST /bookings | userId = {}", userId);
        ResponseEntity<Object> response = client.create(userId, request);
        log.info("<< POST /bookings | userId = {} | status: {}", userId, response.getStatusCode());
        return response;
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@PathVariable @Positive Long bookingId,
                                          @RequestHeader(HEADER_USER_ID) @Positive long userId,
                                          @RequestParam @NotNull boolean approved) {
        log.info(">> PATCH /bookings/{}?approved={} | userId = {}", bookingId, approved, userId);
        ResponseEntity<Object> response = client.approve(bookingId, userId, approved);
        log.info("<< PATCH /bookings/{}?approved={} | userId = {} | status: {}", bookingId, approved, userId, response.getStatusCode());
        return response;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(@PathVariable @Positive Long bookingId,
                                      @RequestHeader(HEADER_USER_ID) @Positive long userId) {
        log.info(">> GET /bookings/{} | userId = {} ", bookingId, userId);
        ResponseEntity<Object> response = client.get(bookingId, userId);
        log.info("<< GET /bookings/{} | userId = {} | status: {}", bookingId, userId, response.getStatusCode());
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getByUser(@RequestHeader(HEADER_USER_ID) @Positive long userId,
                                            @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "10") int size) {
        log.info(">> GET /bookings?state={} | userId = {} ", stateParam, userId);
        BookingState state = parseState(stateParam);
        ResponseEntity<Object> response = client.getByUser(userId, state, from, size);
        log.info("<< GET /bookings?state={} | userId = {} | status: {}", state, userId, response.getStatusCode());
        return response;
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByOwner(@RequestHeader(HEADER_USER_ID) @Positive long userId,
                                             @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        log.info(">> GET /bookings/owner?state={} | userId = {} ", stateParam, userId);
        BookingState state = parseState(stateParam);
        ResponseEntity<Object> response = client.getByOwner(userId, state, from, size);
        log.info("<< GET /bookings/owner?state={} | userId = {} | status: {}", state.name(), userId, response.getStatusCode());
        return response;
    }

    private BookingState parseState(String stateParam) {
        return BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Передан неподдерживаемый параметр: " + stateParam));
    }
}
