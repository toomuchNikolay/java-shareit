package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommentMapper {
    public static Comment toEntity(CommentCreateDto dto, Item item, User author) {
        return Comment.builder()
                .text(dto.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
    }

    public static CommentDto toDto(Comment entity) {
        return CommentDto.builder()
                .id(entity.getId())
                .text(entity.getText())
                .authorName(entity.getAuthor().getName())
                .created(entity.getCreated())
                .build();
    }
}
