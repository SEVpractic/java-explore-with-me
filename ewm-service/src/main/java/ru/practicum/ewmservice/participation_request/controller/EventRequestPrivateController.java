package ru.practicum.ewmservice.participation_request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.participation_request.dto.EventRequestDto;
import ru.practicum.ewmservice.participation_request.service.EventRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class EventRequestPrivateController {
    private final EventRequestService eventRequestService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventRequestDto create(@PathVariable("userId") long userId,
                                  @RequestParam(name = "eventId") long eventId) {
        return eventRequestService.create(userId, eventId);
    }

    @GetMapping
    public List<EventRequestDto> getAll(@PathVariable("userId") long userId) {
        return eventRequestService.getAll(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public EventRequestDto cansel(@PathVariable("userId") long userId,
                                  @PathVariable(name = "requestId") long requestId) {
        return eventRequestService.cansel(userId, requestId);
    }
}
