package ru.practicum.ewmservice.util.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmservice.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.compilation.dto.CompilationIncomeDto;
import ru.practicum.ewmservice.compilation.model.Compilation;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.participation_request.model.EventRequest;
import ru.practicum.statsdto.Stat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationsMapper {
    public static Compilation toCompilation(CompilationIncomeDto dto) {
        Compilation compilation = new Compilation();

        compilation.setPinned(dto.getPinned());
        compilation.setTitle(dto.getTitle());

        return compilation;
    }

    public static CompilationDto toCompilationDto(Compilation compilation,
                                                  Map<Event, List<EventRequest>> confirmedRequests,
                                                  Map<Long, List<Stat>> views) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .events(EventMapper.toEventShortDto(
                        new ArrayList<>(compilation.getEvents()),
                        confirmedRequests,
                        views
                ))
                .build();
    }

    public static List<CompilationDto> toCompilationDto(List<Compilation> compilations,
                                                        Map<Event, List<EventRequest>> confirmedRequests,
                                                        Map<Long, List<Stat>> views) {
        return compilations.stream()
                .map(compilation -> toCompilationDto(
                        compilation,
                        confirmedRequests,
                        views
                ))
                .collect(Collectors.toList());
    }
}
