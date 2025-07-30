package ru.practicum.shareit.request.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ItemRequestResponseDetailsDto extends ItemRequestResponseDto {
    private List<ItemResponseDto> items;
}
