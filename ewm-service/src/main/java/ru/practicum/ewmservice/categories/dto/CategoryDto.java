package ru.practicum.ewmservice.categories.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder(toBuilder = true)
@Getter
public class CategoryDto {
    private final Long id;
    @NotBlank
    @Size(max = 64)
    private final String name;
}
