package ru.practicum.ewmservice.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.event.dto.EventFullDto;
import ru.practicum.ewmservice.event.dto.EventIncomeDto;
import ru.practicum.ewmservice.event.dto.EventShortDto;
import ru.practicum.ewmservice.event.dto.ProcessRequestsDto;
import ru.practicum.ewmservice.event.service.EventService;
import ru.practicum.ewmservice.participation_request.dto.EventRequestDto;
import ru.practicum.ewmservice.util.validation.CreateValidationGroup;
import ru.practicum.ewmservice.util.validation.UpdateValidationGroup;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventFullDto create(@Validated(CreateValidationGroup.class) @RequestBody EventIncomeDto dto,
                               @PathVariable("userId") long userId) {
        return eventService.create(dto, userId);
    }

    @PatchMapping(path = "/{eventId}")
    public EventFullDto update(@Validated(UpdateValidationGroup.class) @RequestBody EventIncomeDto dto,
                               @PathVariable("userId") long userId,
                               @PathVariable("eventId") long eventId) {
        // todo изменить можно только отмененные события или события в состоянии ожидания модерации (Ожидается код ошибки 409)
        // todo дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента (Ожидается код ошибки 409)
        return eventService.privateUpdate(dto, userId, eventId);
    }

    @GetMapping
    public List<EventShortDto> getAll(@PathVariable("userId") long userId) {
        return eventService.getAll(userId);
    }

    @GetMapping(path = "/{eventId}")
    public EventFullDto getBuId(@PathVariable("userId") long userId,
                                @PathVariable("eventId") long eventId) {
        return eventService.getById(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<EventRequestDto> getRequests(@PathVariable("userId") long userId,
                                             @PathVariable("eventId") long eventId) {
        return eventService.getRequests(userId, eventId);
    }

    @PatchMapping("{eventId}/requests")
    public List<EventRequestDto> processRequests(@PathVariable("userId") long userId,
                                @PathVariable("eventId") long eventId,
                                @Valid @RequestBody ProcessRequestsDto dto) {
        return eventService.processRequests(userId, eventId, dto);
    }
}
