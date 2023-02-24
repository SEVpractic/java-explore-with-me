package ru.practicum.ewmservice.participation_request.service;

import ru.practicum.ewmservice.participation_request.dto.EventRequestDto;

import java.util.List;

public interface EventRequestService {
    EventRequestDto create(long userId, long eventId);

    List<EventRequestDto> getAll(long userId);

    EventRequestDto cansel(long userId, long requestId);
}
