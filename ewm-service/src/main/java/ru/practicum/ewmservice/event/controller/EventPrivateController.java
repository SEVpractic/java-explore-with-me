package ru.practicum.ewmservice.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.event.dto.EventFullDto;
import ru.practicum.ewmservice.event.dto.EventIncomeDto;
import ru.practicum.ewmservice.event.dto.EventShortDto;
import ru.practicum.ewmservice.event.service.EventService;
import ru.practicum.ewmservice.util.validation.CreateValidationGroup;
import ru.practicum.ewmservice.util.validation.UpdateValidationGroup;

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
        return eventService.update(dto, userId, eventId);
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
}
