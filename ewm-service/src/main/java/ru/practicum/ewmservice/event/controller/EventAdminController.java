package ru.practicum.ewmservice.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.event.dto.EventFullDto;
import ru.practicum.ewmservice.event.dto.EventIncomeDto;
import ru.practicum.ewmservice.event.model.EventStates;
import ru.practicum.ewmservice.event.service.EventAdminService;
import ru.practicum.ewmservice.util.validation.AdminValidationGroup;
import ru.practicum.ewmservice.util.validation.UpdateValidationGroup;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class EventAdminController {
    private final EventAdminService eventAdminServiceService;

    @PatchMapping(path = "/{eventId}")
    public EventFullDto update(@Validated(UpdateValidationGroup.class) @RequestBody EventIncomeDto dto,
                               @PathVariable("eventId") @Positive long eventId) {
        return eventAdminServiceService.update(dto, eventId);
    }

    @PostMapping(path = "/moderation")
    public List<EventFullDto> updateAll(@Validated(AdminValidationGroup.class) @RequestBody List<EventIncomeDto> dto) {
        return eventAdminServiceService.updateAll(dto);
    }

    @GetMapping
    public List<EventFullDto> findByFilter(@RequestParam(name = "users", required = false) List<Long> userIds,
                                           @RequestParam(name = "states", required = false) List<EventStates> states,
                                           @RequestParam(name = "categories", required = false) List<Long> categories,
                                           @RequestParam(name = "rangeStart", required = false)
                                               LocalDateTime rangeStart,
                                           @RequestParam(name = "rangeEnd", required = false)
                                               LocalDateTime rangeEnd,
                                           @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                           @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        return eventAdminServiceService.getAll(userIds, states, categories, rangeStart, rangeEnd, from, size);
    }

    @GetMapping(path = "/moderation")
    public List<EventFullDto> findWaiting(@RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                          @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        return eventAdminServiceService.getAll(null, List.of(EventStates.PENDING),
                null, null, null, from, size);
    }
}
