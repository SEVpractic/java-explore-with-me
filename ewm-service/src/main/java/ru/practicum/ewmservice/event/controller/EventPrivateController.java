package ru.practicum.ewmservice.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.event.dto.*;
import ru.practicum.ewmservice.event.service.EventPrivateService;
import ru.practicum.ewmservice.participation_request.dto.EventRequestDto;
import ru.practicum.ewmservice.util.validation.CreateValidationGroup;
import ru.practicum.ewmservice.util.validation.UpdateValidationGroup;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class EventPrivateController {
    private final EventPrivateService eventService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventFullDto create(@Validated(CreateValidationGroup.class) @RequestBody EventIncomeDto dto,
                               @PathVariable("userId") @Positive long userId) {
        return eventService.create(dto, userId);
    }

    @PatchMapping(path = "/{eventId}")
    public EventFullDto update(@Validated(UpdateValidationGroup.class) @RequestBody EventIncomeDto dto,
                               @PathVariable("userId") @Positive long userId,
                               @PathVariable("eventId") @Positive long eventId) {
        return eventService.update(dto, userId, eventId);
    }

    @GetMapping
    public List<EventShortDto> getAll(@PathVariable("userId") @Positive long userId) {
        return eventService.getAll(userId);
    }

    @GetMapping(path = "/{eventId}")
    public EventFullDto getBuId(@PathVariable("userId") @Positive long userId,
                                @PathVariable("eventId") @Positive long eventId) {
        return eventService.getById(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<EventRequestDto> getRequests(@PathVariable("userId") @Positive long userId,
                                             @PathVariable("eventId") @Positive long eventId) {
        return eventService.getRequests(userId, eventId);
    }

    @PatchMapping("{eventId}/requests")
    public ProcessRequestResultDto processRequests(@PathVariable("userId") @Positive long userId,
                                                   @PathVariable("eventId") @Positive long eventId,
                                                   @Valid @RequestBody ProcessRequestsDto dto) {
        return eventService.processRequests(userId, eventId, dto);
    }
}
