package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.entity.Booking;

import java.util.Collection;

public interface BookingService {
    BookingDto create(Long userId, BookingCreateDto dto);

    BookingDto approve(Long bookingId, Long userId, boolean approved);

    BookingDto getById(Long bookingId, Long userId);

    Booking findByIdOrThrow(Long bookingId);

    Collection<BookingDto> getBookingsByUser(Long userId, String state, int from, int size);

    Collection<BookingDto> getBookingsByOwner(Long userId, String state, int from, int size);
}
