package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
