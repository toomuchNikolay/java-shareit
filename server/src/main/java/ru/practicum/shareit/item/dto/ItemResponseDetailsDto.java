package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ItemResponseDetailsDto extends ItemResponseDto {
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private Collection<CommentResponseDto> comments;
}
