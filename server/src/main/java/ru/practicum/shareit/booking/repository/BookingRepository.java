package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.entity.Booking;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {
    boolean existsByItem_IdAndBooker_IdAndEndIsBefore(Long itemId, long bookerId, LocalDateTime time);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.id = :itemId
                AND b.status = 'APPROVED'
                AND (
                    (:isStart = true AND b.start > CURRENT_TIMESTAMP)
                    OR
                    (:isStart = false AND b.end < CURRENT_TIMESTAMP)
                )
            ORDER BY
                CASE WHEN :isStart = true THEN b.start END ASC,
                CASE WHEN :isStart = false THEN b.end END DESC
            """)
    Optional<Booking> findNearestBooking(@Param("itemId") Long itemId, @Param("isStart") boolean isStart);
}
