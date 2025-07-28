package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookingMapper {
    public static Booking toEntity(BookingInputDto dto, User booker, Item item) {
        return Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    public static BookingResponseDto toDto(Booking entity) {
        return BookingResponseDto.builder()
                .id(entity.getId())
                .start(entity.getStart())
                .end(entity.getEnd())
                .item(ItemMapper.toDto(entity.getItem()))
                .booker(UserMapper.toDto(entity.getBooker()))
                .status(entity.getStatus())
                .build();
    }

    public static List<BookingResponseDto> toDto(List<Booking> entities) {
        return entities.stream()
                .map(BookingMapper::toDto)
                .toList();
    }
}
