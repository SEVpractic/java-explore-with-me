package ru.practicum.ewmservice.categories.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Builder(toBuilder = true)
@Getter
public class CategoryDto {
    private final Long id;
    @NotBlank
    private final String name;
}
