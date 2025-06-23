package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserCreateDto {
    @NotBlank
    private String name;
    @NotEmpty
    @Email
    private String email;
}
