package ru.practicum.ewmservice.categories.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.categories.dto.CategoryDto;
import ru.practicum.ewmservice.categories.model.Category;
import ru.practicum.ewmservice.categories.storage.CategoryRepo;
import ru.practicum.ewmservice.compilation.storage.CompilationRepo;
import ru.practicum.ewmservice.event.storage.EventRepo;
import ru.practicum.ewmservice.event.storage.EventStateRepo;
import ru.practicum.ewmservice.event.storage.LocationRepo;
import ru.practicum.ewmservice.participation_request.storage.EventRequestRepo;
import ru.practicum.ewmservice.participation_request.storage.EventRequestStatsRepo;
import ru.practicum.ewmservice.user.storage.UserRepo;
import ru.practicum.ewmservice.util.UtilService;
import ru.practicum.ewmservice.util.exceptions.OperationFailedException;
import ru.practicum.ewmservice.util.mappers.CategoryMapper;
import ru.practicum.statsclient.StatsClientImpl;

import java.util.List;

@Service
@Slf4j
@Transactional
public class CategoryServiceImpl extends UtilService implements CategoryService{
    public CategoryServiceImpl(UserRepo userRepo,
                               EventRepo eventRepo,
                               CategoryRepo categoryRepo,
                               LocationRepo locationRepo,
                               EventStateRepo eventStateRepo,
                               CompilationRepo compilationRepo,
                               EventRequestRepo eventRequestRepo,
                               EventRequestStatsRepo eventRequestStatsRepo,
                               StatsClientImpl statsClient) {
        super(userRepo, eventRepo, categoryRepo, locationRepo, eventStateRepo,
                compilationRepo, eventRequestRepo, eventRequestStatsRepo, statsClient);
    }

    @Override
    public CategoryDto create(CategoryDto dto) {
        Category category = save(dto);
        log.info("Создана категория c id = {} ", category.getId());
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto update(CategoryDto dto, long catId) {
        findCategoryOrThrow(catId);
        Category category = save(dto);
        log.info("Обновлена категория c id = {} ", category.getId());
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public void delete(long catId) {
        Category category = findCategoryOrThrow(catId);
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
        Category category = findCategoryOrThrow(catId);
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
