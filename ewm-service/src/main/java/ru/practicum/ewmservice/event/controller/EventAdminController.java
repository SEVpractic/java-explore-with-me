package ru.practicum.ewmservice.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.event.dto.EventFullDto;
import ru.practicum.ewmservice.event.dto.EventIncomeDto;
import ru.practicum.ewmservice.event.model.EventStates;
import ru.practicum.ewmservice.event.service.EventService;
import ru.practicum.ewmservice.util.validation.UpdateValidationGroup;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class EventAdminController {
    private final EventService eventService;

    @PatchMapping(path = "/{eventId}")
    public EventFullDto update(@Validated(UpdateValidationGroup.class) @RequestBody EventIncomeDto dto,
                               @PathVariable("eventId") long eventId) {
        return eventService.adminUpdate(dto, eventId);
    }

    @GetMapping
    public List<EventFullDto> findByFilter(@RequestParam(name = "users") List<Long> userIds,
                                           @RequestParam(name = "states") List<EventStates> states,
                                           @RequestParam(name = "categories") List<Long> categories,
                                           @RequestParam(name = "rangeStart", required = false)
                                               LocalDateTime rangeStart,
                                           @RequestParam(name = "rangeEnd", required = false)
                                               LocalDateTime rangeEnd,
                                           @RequestParam(name = "from", defaultValue = "0") int from,
                                           @RequestParam(name = "size", defaultValue = "10") int size) {
        return eventService.getAllAdmin(userIds, states, categories, rangeStart, rangeEnd, from, size);
    }
}
