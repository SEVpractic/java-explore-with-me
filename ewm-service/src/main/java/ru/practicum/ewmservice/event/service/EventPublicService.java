package ru.practicum.ewmservice.event.service;

import ru.practicum.ewmservice.event.dto.EventFullDto;
import ru.practicum.ewmservice.event.dto.EventShortDto;
import ru.practicum.ewmservice.event.dto.EventSorts;

import java.time.LocalDateTime;
import java.util.List;

public interface EventPublicService {
    List<EventShortDto> getAll(String text,
                               List<Long> categories,
                               boolean paid,
                               LocalDateTime rangeStart,
                               LocalDateTime rangeEnd,
                               boolean onlyAvailable,
                               EventSorts sort,
                               int from, int size,
                               String ip);

    EventFullDto getById(long eventId, String ip);
}
