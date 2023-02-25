package ru.practicum.ewmservice.event.service;

import ru.practicum.ewmservice.event.dto.*;
import ru.practicum.ewmservice.participation_request.dto.EventRequestDto;

import java.util.List;

public interface EventPrivateService {

    EventFullDto create(EventIncomeDto dto, long userId);

    EventFullDto privateUpdate(EventIncomeDto dto, long userId, long eventId);

    List<EventShortDto> getAll(long userId);

    EventFullDto getById(long userId, long eventId);

    List<EventRequestDto> getRequests(long userId, long eventId);

    ProcessRequestResultDto processRequests(long userId, long eventId, ProcessRequestsDto dto);

}
