package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ModifyItemRequest {
    @Size(max = 255)
    private String name;

    @Size(max = 500)
    private String description;

    private Boolean available;
}
