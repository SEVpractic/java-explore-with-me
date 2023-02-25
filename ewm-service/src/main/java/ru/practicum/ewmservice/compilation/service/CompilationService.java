package ru.practicum.ewmservice.compilation.service;

import ru.practicum.ewmservice.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.compilation.dto.CompilationIncomeDto;

import java.util.List;

public interface CompilationService {

    CompilationDto create(CompilationIncomeDto dto);

    CompilationDto update(CompilationIncomeDto dto, long compId);

    CompilationDto delete(long compId);

    List<CompilationDto> getAll(boolean pinned, int from, int size);

    CompilationDto getById(long compId);
}
