package ru.practicum.shareit.booking;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository repository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private ItemServiceImpl itemService;

    @InjectMocks
    private BookingServiceImpl service;

    private User ownerUser;
    private User someUser;
    private Item availableItem;
    private Item bookedItem;
    private Booking firstBooking;
    private Booking secondBooking;

    @BeforeEach
    void setUp() {
        ownerUser = User.builder()
                .id(1L)
                .name("Owner")
                .email("mail@box.com")
                .build();
        someUser = User.builder()
                .id(2L)
                .name("Name some user")
                .email("thismailsomeuser@box.com")
                .build();

        availableItem = Item.builder()
                .id(1L)
                .name("Name availableItem")
                .description("specification availableItem")
                .available(true)
                .owner(ownerUser)
                .request(null)
                .comments(Collections.emptyList())
                .build();
        bookedItem = Item.builder()
                .id(2L)
                .name("Name bookedItem")
                .description("specification bookedItem")
                .available(false)
                .owner(ownerUser)
                .request(null)
                .comments(Collections.emptyList())
                .build();

        firstBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 7, 29, 0, 0, 0))
                .end(LocalDateTime.of(2025, 7, 29, 10, 0, 0))
                .item(availableItem)
                .booker(someUser)
                .status(BookingStatus.APPROVED)
                .build();
        secondBooking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.of(2025, 7, 29, 12, 0, 0))
                .end(LocalDateTime.of(2025, 7, 29, 20, 0, 0))
                .item(availableItem)
                .booker(someUser)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void create_whenRequestValid_thenReturnedBooking() {
        long userId = 2L;
        BookingInputDto dto = BookingInputDto.builder()
                .start(LocalDateTime.of(2025, 8, 1, 12, 0, 0))
                .end(LocalDateTime.of(2025, 8, 10, 12, 0, 0))
                .itemId(1L)
                .build();
        Booking booking = BookingMapper.toEntity(dto, someUser, availableItem);
        when(userService.findUserOrThrow(userId)).thenReturn(someUser);
        when(itemService.findItemOrThrow(1L)).thenReturn(availableItem);
        when(repository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto response = service.create(userId, dto);

        assertThat(response, notNullValue());
        assertThat(response.getStart(), equalTo(dto.getStart()));
        assertThat(response.getEnd(), equalTo(dto.getEnd()));
        assertThat(response.getItem().getId(), equalTo(dto.getItemId()));
        assertThat(response.getBooker().getId(), equalTo(userId));
        assertThat(response.getStatus(), equalTo(BookingStatus.WAITING));
        verify(userService, times(1)).findUserOrThrow(anyLong());
        verify(itemService, times(1)).findItemOrThrow(anyLong());
        verify(repository).save(any(Booking.class));
    }

    @Test
    void create_whenRequestNotValidDates_thenValidationExceptionThrown() {
        long userId = 2L;
        BookingInputDto dto = BookingInputDto.builder()
                .start(LocalDateTime.of(2025, 8, 1, 12, 0, 0))
                .end(LocalDateTime.of(2025, 8, 1, 12, 0, 0))
                .itemId(1L)
                .build();

        assertThrows(ValidationException.class, () -> service.create(userId, dto));
        verify(repository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenItemNotAvailable_thenValidationExceptionThrown() {
        long userId = 2L;
        BookingInputDto dto = BookingInputDto.builder()
                .start(LocalDateTime.of(2025, 8, 1, 12, 0, 0))
                .end(LocalDateTime.of(2025, 8, 10, 12, 0, 0))
                .itemId(2L)
                .build();
        when(userService.findUserOrThrow(userId)).thenReturn(someUser);
        when(itemService.findItemOrThrow(2L)).thenReturn(bookedItem);

        assertThrows(ValidationException.class, () -> service.create(userId, dto));
        verify(userService, times(1)).findUserOrThrow(anyLong());
        verify(itemService, times(1)).findItemOrThrow(anyLong());
        verify(repository, never()).save(any(Booking.class));
    }

    @Test
    void approve_whenRequestTrue_thenSetStatusApproved() {
        Long bookingId = 2L;
        long userId = 1L;
        when(repository.findById(bookingId)).thenReturn(Optional.of(secondBooking));

        BookingResponseDto response = service.approve(bookingId, userId, true);

        assertThat(response.getId(), equalTo(secondBooking.getId()));
        assertThat(response.getStatus(), equalTo(BookingStatus.APPROVED));
        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    void approve_whenRequestFalse_thenSetStatusRejected() {
        Long bookingId = 2L;
        long userId = 1L;
        when(repository.findById(bookingId)).thenReturn(Optional.of(secondBooking));
        BookingResponseDto response = service.approve(bookingId, userId, false);

        assertThat(response.getId(), equalTo(secondBooking.getId()));
        assertThat(response.getStatus(), equalTo(BookingStatus.REJECTED));
        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    void approve_whenRequestNotWaitingStatus_thenValidationExceptionThrown() {
        Long bookingId = 2L;
        long userId = 1L;
        secondBooking.setStatus(BookingStatus.CANCELED);
        when(repository.findById(bookingId)).thenReturn(Optional.of(secondBooking));

        assertThrows(ValidationException.class, () -> service.approve(bookingId, userId, true));
        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    void approve_whenRequestNotOwnerId_thenAccessDeniedExceptionThrown() {
        Long bookingId = 2L;
        long userId = 2L;
        when(repository.findById(bookingId)).thenReturn(Optional.of(secondBooking));

        assertThrows(AccessDeniedException.class, () -> service.approve(bookingId, userId, true));
        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    void getById_whenBookerId_thenReturnedBooking() {
        Long bookingId = 1L;
        long userId = 2L;
        when(repository.findById(bookingId)).thenReturn(Optional.of(firstBooking));

        BookingResponseDto response = service.getById(bookingId, userId);

        assertThat(response, equalTo(BookingMapper.toDto(firstBooking)));
        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    void getById_whenOwnerId_thenReturnedBooking() {
        Long bookingId = 1L;
        long userId = 1L;
        when(repository.findById(bookingId)).thenReturn(Optional.of(firstBooking));

        BookingResponseDto response = service.getById(bookingId, userId);

        assertThat(response, equalTo(BookingMapper.toDto(firstBooking)));
        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    void getById_whenNotBookerOrOwnerId_thenAccessDeniedExceptionThrown() {
        Long bookingId = 1L;
        long userId = 10L;
        when(repository.findById(bookingId)).thenReturn(Optional.of(firstBooking));

        assertThrows(AccessDeniedException.class, () -> service.getById(bookingId, userId));
        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    void getBookingsByUser_whenUserFound_thenReturnedCollection() {
        long userId = 2L;
        String state = "FUTURE";
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> requests = List.of(secondBooking, firstBooking);
        Page<Booking> page = new PageImpl<>(requests, pageable, requests.size());
        when(userService.findUserOrThrow(userId)).thenReturn(someUser);
        when(repository.findAll(any(BooleanExpression.class), any(Pageable.class))).thenReturn(page);

        List<BookingResponseDto> response = service.getBookingsByUser(userId, state, 0, 10);

        assertThat(response, hasSize(2));
        assertThat(response.getFirst(), equalTo(BookingMapper.toDto(secondBooking)));
        assertThat(response, hasItem(BookingMapper.toDto(firstBooking)));
        verify(userService, times(1)).findUserOrThrow(anyLong());
        verify(repository, times(1)).findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test
    void getBookingsByUser_whenNotValidState_thenValidationExceptionThrown() {
        long userId = 2L;
        String state = "CURRENT AND FUTURE";
        when(userService.findUserOrThrow(userId)).thenReturn(someUser);

        assertThrows(ValidationException.class, () -> service.getBookingsByUser(userId, state, 0, 10));
        verify(repository, never()).findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test
    void getBookingsByOwner_whenOwnerFound_thenReturnedCollection() {
        long userId = 1;
        String state = "Waiting";
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> requests = List.of(secondBooking);
        Page<Booking> page = new PageImpl<>(requests, pageable, requests.size());
        when(userService.findUserOrThrow(userId)).thenReturn(ownerUser);
        when(itemService.hasUserAnyItems(userId)).thenReturn(true);
        when(repository.findAll(any(BooleanExpression.class), any(Pageable.class))).thenReturn(page);

        List<BookingResponseDto> response = service.getBookingsByOwner(userId, state, 0, 10);

        assertThat(response, hasSize(1));
        assertThat(response, hasItem(BookingMapper.toDto(secondBooking)));
        verify(userService, times(1)).findUserOrThrow(anyLong());
        verify(itemService, times(1)).hasUserAnyItems(anyLong());
        verify(repository, times(1)).findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test
    void getBookingsByOwner_whenOwnerNotFound_thenAccessDeniedExceptionThrown() {
        long userId = 2;
        String state = "ALL";
        when(userService.findUserOrThrow(userId)).thenReturn(someUser);
        when(itemService.hasUserAnyItems(userId)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> service.getBookingsByOwner(userId, state, 0, 10));
        verify(userService, times(1)).findUserOrThrow(anyLong());
        verify(itemService, times(1)).hasUserAnyItems(anyLong());
        verify(repository, never()).findAll(any(BooleanExpression.class), any(Pageable.class));
    }
}
