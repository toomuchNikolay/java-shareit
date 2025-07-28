package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.entity.ItemRequest;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    Page<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(long requestorId, Pageable page);

    Page<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(long requestorId, Pageable page);
}
