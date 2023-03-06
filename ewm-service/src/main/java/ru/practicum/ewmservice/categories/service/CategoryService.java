package ru.practicum.ewmservice.categories.service;

import ru.practicum.ewmservice.categories.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(CategoryDto dto);

    CategoryDto update(CategoryDto dto, long catId);

    void delete(long catId);

    List<CategoryDto> getAll(int from, int size);

    CategoryDto get(long catId);
}
