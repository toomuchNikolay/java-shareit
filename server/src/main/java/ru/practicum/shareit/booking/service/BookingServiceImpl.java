package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.QBooking;
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
import java.util.List;
import java.util.Objects;

import static ru.practicum.shareit.exception.errors.ErrorMessage.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public BookingResponseDto create(long userId, BookingInputDto dto) {
        validateTimeBooking(dto);
        User booker = userService.findUserOrThrow(userId);
        Item item = itemService.findItemOrThrow(dto.getItemId());
        if (item.getAvailable().equals(false)) {
            log.warn("Попытка забронировать недоступную вещь");
            throw new ValidationException(BOOKING_ITEM_UNAVAILABLE);
        }
        Booking booking = bookingRepository.save(BookingMapper.toEntity(dto, booker, item));
        log.info("Добавлена сущность Booking: {}", booking);
        return BookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingResponseDto approve(Long bookingId, long userId, boolean approved) {
        Booking booking = findByIdOrThrow(bookingId);
        if (booking.getStatus().equals(BookingStatus.REJECTED) || booking.getStatus().equals(BookingStatus.CANCELED)) {
            log.warn("Попытка подтвердить бронирование со статусом {}", booking.getStatus());
            throw new ValidationException(ONLY_STATUS_WAITING_APPROVED);
        }
        if (!isUserOwner(booking, userId)) {
            log.warn("Попытка подтвердить бронирование id={} пользователем id={} вместо владельца", bookingId, userId);
            throw new AccessDeniedException(ONLY_OWNER_APPROVED);
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        log.info("Обновлена сущность Booking: {}", booking);
        return BookingMapper.toDto(booking);
    }

    @Override
    public BookingResponseDto getById(Long bookingId, long userId) {
        Booking booking = findByIdOrThrow(bookingId);
        if (!(isUserOwner(booking, userId) || isUserBooker(booking, userId))) {
            log.warn("Попытка посмотреть бронирование без прав пользователем id={}", userId);
            throw new AccessDeniedException(ONLY_BOOKER_OR_OWNER_VIEW);
        }
        log.info("Возвращена сущность Booking: {}", booking);
        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingResponseDto> getBookingsByUser(long userId, String state, int from, int size) {
        User user = userService.findUserOrThrow(userId);
        List<Booking> result = findBookingsByState(user.getId(), state, from, size, true).getContent();
        log.info("Возвращен список в размере {} найденных сущностей Booking пользователю id={}", result.size(), userId);
        return BookingMapper.toDto(result);
    }

    @Override
    public List<BookingResponseDto> getBookingsByOwner(long userId, String state, int from, int size) {
        User owner = userService.findUserOrThrow(userId);
        if (!itemService.hasUserAnyItems(owner.getId())) {
            log.warn("Попытка просмотреть бронирования пользователем id={} не являющимся владельцем вещей", userId);
            throw new AccessDeniedException(ONLY_OWNER_VIEW);
        }
        List<Booking> result = findBookingsByState(owner.getId(), state, from, size, false).getContent();
        log.info("Возвращен список в размере {} найденных сущностей Booking владельцу id={}", result.size(), userId);
        return BookingMapper.toDto(result);
    }

    @Override
    public Booking findByIdOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(BOOKING_NOT_FOUND));
    }

    private void validateTimeBooking(BookingInputDto dto) {
        if (dto.getStart().isAfter(dto.getEnd()) || dto.getStart().equals(dto.getEnd())) {
            log.warn("Попытка указать некорректные даты при бронировании");
            throw new ValidationException(BOOKING_TIME_INCORRECT);
        }
    }

    private boolean isUserOwner(Booking booking, long userId) {
        return Objects.equals(booking.getItem().getOwner().getId(), userId);
    }

    private boolean isUserBooker(Booking booking, long userId) {
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

    private Page<Booking> findBookingsByState(long userId, String state, int from, int size, boolean isBooker) {
        BookingState bookingState = validateBookingState(state);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.DESC, "start"));
        LocalDateTime currentTime = LocalDateTime.now();

        BooleanExpression byUser = isBooker
                ? QBooking.booking.booker.id.eq(userId)
                : QBooking.booking.item.owner.id.eq(userId);

        BooleanExpression byState = switch (bookingState) {
            case CURRENT -> QBooking.booking.status.eq(BookingStatus.APPROVED)
                    .and(QBooking.booking.start.before(currentTime))
                    .and(QBooking.booking.end.after(currentTime));
            case FUTURE -> QBooking.booking.status.eq(BookingStatus.APPROVED)
                    .and(QBooking.booking.start.after(currentTime));
            case PAST -> QBooking.booking.status.eq(BookingStatus.APPROVED)
                    .and(QBooking.booking.end.before(currentTime));
            case REJECTED -> QBooking.booking.status.eq(BookingStatus.REJECTED);
            case WAITING -> QBooking.booking.status.eq(BookingStatus.WAITING);
            case ALL -> null;
        };
        return bookingRepository.findAll(byState != null ? byUser.and(byState) : byUser, page);
    }
}
