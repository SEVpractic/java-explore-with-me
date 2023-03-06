package ru.practicum.ewmservice.categories.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.categories.dto.CategoryDto;
import ru.practicum.ewmservice.categories.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAll(@RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                    @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        return categoryService.getAll(from, size);
    }

    @GetMapping(path = "/{catId}")
    public CategoryDto get(@PathVariable("catId") @Positive long catId) {
        return categoryService.get(catId);
    }
}
