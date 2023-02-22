package ru.practicum.ewmservice.categories.dto;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class CategoryDto {
    private final Long id;
    private final String name;
}
