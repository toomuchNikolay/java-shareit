package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.entity.Booking;

import java.util.List;

public interface BookingService {
    BookingResponseDto create(long userId, BookingInputDto dto);

    BookingResponseDto approve(Long bookingId, long userId, boolean approved);

    BookingResponseDto getById(Long bookingId, long userId);

    List<BookingResponseDto> getBookingsByUser(long userId, String state, int from, int size);

    List<BookingResponseDto> getBookingsByOwner(long userId, String state, int from, int size);

    Booking findByIdOrThrow(Long bookingId);
}
