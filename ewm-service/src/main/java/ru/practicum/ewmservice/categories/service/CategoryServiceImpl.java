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
import ru.practicum.ewmservice.event.storage.EventRepo;
import ru.practicum.ewmservice.util.UtilService;
import ru.practicum.ewmservice.util.exceptions.OperationFailedException;
import ru.practicum.ewmservice.util.mappers.CategoryMapper;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CategoryServiceImpl implements CategoryService {
    private final UtilService utilService;
    private final CategoryRepo categoryRepo;
    private final EventRepo eventRepo;

    @Override
    @Transactional
    public CategoryDto create(CategoryDto dto) {
        Category category = save(dto);
        log.info("Создана категория c id = {} ", category.getId());

        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto update(CategoryDto dto, long catId) {
        Category category = utilService.findCategoryOrThrow(catId);

        if (!Objects.equals(dto.getName(), category.getName())) category.setName(dto.getName());
        log.info("Обновлена категория c id = {} ", category.getId());

        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public void delete(long catId) {
        Category category = utilService.findCategoryOrThrow(catId);
        checkDeleteAvailable(category);
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
        Category category = utilService.findCategoryOrThrow(catId);
        log.info("Возвращена категория c id = {} ", catId);
        return CategoryMapper.toCategoryDto(category);
    }

    private Category save(CategoryDto dto) {
        return categoryRepo.save(CategoryMapper.toCategory(dto));
    }

    private void checkDeleteAvailable(Category category) {
        if (!eventRepo.findAllByCategory(category).isEmpty()) {
            throw new OperationFailedException(
                    String.format("Невозможно удалить категорию id = %s. Существуют события, связанные с категорией",
                            category.getId())
            );
        }
    }
}
