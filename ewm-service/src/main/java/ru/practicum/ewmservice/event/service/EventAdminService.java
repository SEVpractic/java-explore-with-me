package ru.practicum.ewmservice.event.service;

import ru.practicum.ewmservice.event.dto.EventFullDto;
import ru.practicum.ewmservice.event.dto.EventIncomeDto;
import ru.practicum.ewmservice.event.model.EventStates;

import java.time.LocalDateTime;
import java.util.List;

public interface EventAdminService {
    EventFullDto update(EventIncomeDto dto, long eventId);


    List<EventFullDto> getAll(List<Long> userIds,
                              List<EventStates> states,
                              List<Long> categories,
                              LocalDateTime rangeStart,
                              LocalDateTime rangeEnd,
                              int from, int size);
}
