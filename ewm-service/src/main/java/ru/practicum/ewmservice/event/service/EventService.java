package ru.practicum.ewmservice.event.service;

import ru.practicum.ewmservice.event.dto.*;
import ru.practicum.ewmservice.event.model.EventStates;
import ru.practicum.ewmservice.participation_request.dto.EventRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventFullDto create(EventIncomeDto dto, long userId);

    EventFullDto privateUpdate(EventIncomeDto dto, long userId, long eventId);

    EventFullDto adminUpdate(EventIncomeDto dto, long eventId);

    List<EventShortDto> getAll(long userId);

    List<EventShortDto> getAllPublic(String text, List<Long> categories, boolean paid,
                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                               boolean onlyAvailable, EventSorts sort,
                               int from, int size);

    EventFullDto getById(long userId, long eventId);

    EventFullDto getPublicById(long eventId);

    List<EventFullDto> getAllAdmin(List<Long> userIds, List<EventStates> states, List<Long> categories,
                                   LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    List<EventRequestDto> getRequests(long userId, long eventId);

    ProcessRequestResultDto processRequests(long userId, long eventId, ProcessRequestsDto dto);

}
