package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

import static ru.practicum.shareit.exception.errors.ErrorMessage.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public BookingDto create(Long userId, BookingCreateDto dto) {
        validateTimeBooking(dto);
        User booker = userService.findByIdOrThrow(userId);
        Item item = itemService.findByIdOrThrow(dto.getItemId());
        if (item.getAvailable().equals(false)) {
            throw new ValidationException(BOOKING_ITEM_UNAVAILABLE);
        }
        Booking booking = BookingMapper.toEntity(dto, booker, item);
        repository.save(booking);
        log.info("Добавлена сущность Booking: {}", booking);
        return BookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingDto approve(Long bookingId, Long userId, boolean approved) {
        Booking booking = findByIdOrThrow(bookingId);
        if (!isUserOwner(booking, userId)) {
            throw new AccessDeniedException(ONLY_OWNER_APPROVED);
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        log.info("Обновлена сущность Booking: {}", booking);
        return BookingMapper.toDto(booking);
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = findByIdOrThrow(bookingId);
        if (!(isUserOwner(booking, userId) || isUserBooker(booking, userId))) {
            log.warn("Попытка посмотреть бронирование без прав пользователем id={}", userId);
            throw new AccessDeniedException(ONLY_BOOKER_OR_OWNER_VIEW);
        }
        return BookingMapper.toDto(booking);
    }

    @Override
    public Booking findByIdOrThrow(Long bookingId) {
        return repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(BOOKING_NOT_FOUND));
    }

    @Override
    public Collection<BookingDto> getBookingsByUser(Long userId, String state, int from, int size) {
        return findBookingsByState(userId, state, from, size, true)
                .stream()
                .map(BookingMapper::toDto)
                .toList();
    }

    @Override
    public Collection<BookingDto> getBookingsByOwner(Long userId, String state, int from, int size) {
        if (!itemService.hasUserAnyItems(userId)) {
            throw new AccessDeniedException(ONLY_OWNER_VIEW);
        }
        return findBookingsByState(userId, state, from, size, false)
                .stream()
                .map(BookingMapper::toDto)
                .toList();
    }

    private void validateTimeBooking(BookingCreateDto dto) {
        if (dto.getStart().isAfter(dto.getEnd()) || dto.getStart() == dto.getEnd()) {
            log.warn("Попытка указать некорректные даты при бронировании");
            throw new ValidationException(BOOKING_TIME_INCORRECT);
        }
    }

    private boolean isUserOwner(Booking booking, Long userId) {
        return Objects.equals(booking.getItem().getOwner().getId(), userId);
    }

    private boolean isUserBooker(Booking booking, Long userId) {
        return Objects.equals(booking.getBooker().getId(), userId);
    }

    private BookingState validateBookingState(String state) {
        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Попытка указать некорректный тип поиска");
            throw new ValidationException(BOOKING_STATE_INCORRECT);
        }
    }

    private Page<Booking> findBookingsByState(Long userId, String state, int from, int size, boolean isBooker) {
        BookingState bookingState = validateBookingState(state);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        LocalDateTime currentTime = LocalDateTime.now();

        return switch (bookingState) {
            case ALL -> isBooker
                    ? repository.findByBooker_IdOrderByStartDesc(userId, page)
                    : repository.findByItemOwner_IdOrderByStartDesc(userId, page);
            case CURRENT -> isBooker
                    ? repository.findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, currentTime, currentTime, page)
                    : repository.findByItemOwner_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, currentTime, currentTime, page);
            case FUTURE -> isBooker
                    ? repository.findByBooker_IdAndStartIsAfterOrderByStartDesc(userId, currentTime, page)
                    : repository.findByItemOwner_IdAndStartIsAfterOrderByStartDesc(userId, currentTime, page);
            case PAST -> isBooker
                    ? repository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(userId, currentTime, page)
                    : repository.findByItemOwner_IdAndEndIsBeforeOrderByStartDesc(userId, currentTime, page);
            case REJECTED -> isBooker
                    ? repository.findByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, page)
                    : repository.findByItemOwner_IdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, page);
            case WAITING -> isBooker
                    ? repository.findByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, page)
                    : repository.findByItemOwner_IdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, page);
        };
    }
}
