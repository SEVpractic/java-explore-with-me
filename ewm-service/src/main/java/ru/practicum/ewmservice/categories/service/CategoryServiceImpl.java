package ru.practicum.ewmservice.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.categories.dto.CategoryDto;
import ru.practicum.ewmservice.categories.model.Category;
import ru.practicum.ewmservice.categories.storage.CategoryRepo;
import ru.practicum.ewmservice.util.mappers.CategoryMapper;
import ru.practicum.ewmservice.util.exceptions.EntityNotExistException;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepo categoryRepo;

    @Override
    public CategoryDto create(CategoryDto dto) {
        Category category = save(dto);
        log.info("Создана категория c id = {} ", category.getId());
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto update(CategoryDto dto, long catId) {
        findOrThrow(catId);
        Category category = save(dto);
        log.info("Обновлена категория c id = {} ", category.getId());
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public void delete(long catId) {
        findOrThrow(catId);
        categoryRepo.deleteById(catId);
        log.info("Удален пользователь c id = {} ", catId);
    }

    @Override
    public List<CategoryDto> getAll(int from, int size) {
        List<Category> categories;

        Pageable pageable = PageRequest.of(
                from == 0 ? 0 : (from / size),
                size,
                Sort.by(Sort.Direction.ASC, "id")
        );

        categories = categoryRepo.findAll(pageable).toList();
        log.info("Возвращен список всех категорий");

        return CategoryMapper.toCategoryDto(categories);
    }

    @Override
    public CategoryDto get(long catId) {
        Category category = findOrThrow(catId);
        log.info("Возвращена категория c id = {} ", catId);
        return CategoryMapper.toCategoryDto(category);
    }

    private Category save(CategoryDto dto) {
        return categoryRepo.save(CategoryMapper.toCategory(dto));
    }

    private Category findOrThrow(long catId) {
        return categoryRepo.findById(catId)
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Категория c id = %s не существует", catId))
                );
    }
}
