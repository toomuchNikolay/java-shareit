package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.item.entity.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {
    boolean existsByOwner_Id(Long ownerId);

    Collection<Item> findAllByOwner_Id(Long ownerId);
}
