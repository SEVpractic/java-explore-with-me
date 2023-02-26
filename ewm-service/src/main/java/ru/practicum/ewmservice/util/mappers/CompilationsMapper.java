package ru.practicum.ewmservice.util.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmservice.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.compilation.dto.CompilationIncomeDto;
import ru.practicum.ewmservice.compilation.model.Compilation;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationsMapper {
    public static Compilation toCompilation(CompilationIncomeDto dto) {
        Compilation compilation = new Compilation();

        compilation.setPinned(dto.getPinned());
        compilation.setTitle(dto.getTitle());

        return compilation;
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .events(EventMapper.toEventShortDto(
                        compilation.getEvents().stream().collect(Collectors.toList())
                ))
                .build();
    }

    public static List<CompilationDto> toCompilationDto(List<Compilation> compilations) {
        return compilations.stream()
                .map(CompilationsMapper::toCompilationDto)
                .collect(Collectors.toList());
    }
}
