package ru.practicum.ewmservice.event.service;

import ru.practicum.ewmservice.event.dto.EventFullDto;
import ru.practicum.ewmservice.event.dto.EventIncomeDto;
import ru.practicum.ewmservice.event.dto.EventShortDto;

import java.util.List;

public interface EventService {

    EventFullDto create(EventIncomeDto dto, long userId);

    EventFullDto update(EventIncomeDto dto, long userId, long eventId);

    List<EventShortDto> getAll(long userId);

    EventFullDto getById(long userId, long eventId);

}
