package ru.practicum.ewmservice.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.compilation.dto.CompilationIncomeDto;
import ru.practicum.ewmservice.compilation.service.CompilationService;
import ru.practicum.ewmservice.util.validation.CreateValidationGroup;
import ru.practicum.ewmservice.util.validation.UpdateValidationGroup;

import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CompilationDto create(@Validated(CreateValidationGroup.class) @RequestBody CompilationIncomeDto dto) {
        return compilationService.create(dto);
    }

    @PatchMapping(path = "/{compId}")
    public CompilationDto update(@Validated(UpdateValidationGroup.class) @RequestBody CompilationIncomeDto dto,
                                 @PathVariable("compId") long compId) {
        return compilationService.update(dto, compId);
    }

    @DeleteMapping(path = "/{compId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public CompilationDto delete(@PathVariable("compId") @Positive long compId) {
        return compilationService.delete(compId);
    }
}
