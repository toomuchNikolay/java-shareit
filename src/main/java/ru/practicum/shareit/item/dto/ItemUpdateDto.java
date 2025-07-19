package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemUpdateDto {
    @Size(max = 255)
    private String name;

    @Size(max = 500)
    private String description;

    private Boolean available;
}
