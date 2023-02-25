package ru.practicum.ewmservice.compilation.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.ewmservice.event.dto.EventShortDto;

import java.util.List;

@Builder(toBuilder = true)
@Getter
public class CompilationDto {
    private final Long id;
    private final List<EventShortDto> events;
    private final boolean pinned;
    private final String title;
}
