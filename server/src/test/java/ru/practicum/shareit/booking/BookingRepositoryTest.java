package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private Item item;
    private Booking firstBooking;
    private Booking secondBooking;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .id(1L)
                .name("Name user")
                .email("mail@box.com")
                .build()
        );

        item = itemRepository.save(Item.builder()
                .id(1L)
                .name("Name item")
                .description("specification item")
                .available(true)
                .owner(user)
                .request(null)
                .comments(Collections.emptyList())
                .build()
        );

        firstBooking = bookingRepository.save(Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build());
        secondBooking = bookingRepository.save(Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build());
    }

    @Test
    void existsByItem_IdAndBooker_IdAndEndIsBefore_whenBookingExists_thenReturnedTrue() {
        boolean result = bookingRepository.existsByItem_IdAndBooker_IdAndEndIsBefore(
                item.getId(),
                user.getId(),
                LocalDateTime.now());

        assertThat(result, equalTo(true));
    }

    @Test
    void existsByItem_IdAndBooker_IdAndEndIsBefore_whenBookingNotExists_thenReturnedFalse() {
        boolean result = bookingRepository.existsByItem_IdAndBooker_IdAndEndIsBefore(
                item.getId(),
                user.getId(),
                LocalDateTime.now().minusDays(10));

        assertThat(result, equalTo(false));
    }

    @Test
    void findNearestBooking_whenBookingFoundAndNotIsStart_thenReturnedLastBooking() {
        Optional<Booking> result = bookingRepository.findNearestBooking(item.getId(), false);

        assertThat(result, equalTo(Optional.of(firstBooking)));
    }

    @Test
    void findNearestBooking_whenBookingFoundAndIsStart_thenReturnedNextBooking() {
        Optional<Booking> result = bookingRepository.findNearestBooking(item.getId(), true);

        assertThat(result, equalTo(Optional.of(secondBooking)));
    }

    @Test
    void findNearestBooking_whenBookingNotFound_thenReturnedNull() {
        bookingRepository.delete(secondBooking);
        Optional<Booking> result = bookingRepository.findNearestBooking(item.getId(), true);

        assertThat(result, equalTo(Optional.empty()));
    }
}
