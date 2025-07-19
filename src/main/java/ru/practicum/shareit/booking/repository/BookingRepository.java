package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByItem_IdAndBooker_IdAndEndIsBefore(Long itemId, Long bookerId, LocalDateTime time);

    Optional<Booking> findFirstByItem_IdAndStatusAndEndIsBeforeOrderByEndDesc(Long itemId, BookingStatus status, LocalDateTime time);

    Optional<Booking> findFirstByItem_IdAndStatusAndStartIsAfterOrderByStartAsc(Long itemId, BookingStatus status, LocalDateTime time);

    Page<Booking> findByBooker_IdOrderByStartDesc(Long bookerId, Pageable page);

    Page<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end, Pageable page);

    Page<Booking> findByBooker_IdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime time, Pageable page);

    Page<Booking> findByBooker_IdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime time, Pageable page);

    Page<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable page);

    Page<Booking> findByItemOwner_IdOrderByStartDesc(Long ownerId, Pageable page);

    Page<Booking> findByItemOwner_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long ownerId, LocalDateTime start, LocalDateTime end, Pageable page);

    Page<Booking> findByItemOwner_IdAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime time, Pageable page);

    Page<Booking> findByItemOwner_IdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime time, Pageable page);

    Page<Booking> findByItemOwner_IdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable page);
}
