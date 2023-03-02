package ru.practicum.ewmservice.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.event.dto.EventFullDto;
import ru.practicum.ewmservice.event.dto.EventShortDto;
import ru.practicum.ewmservice.event.dto.EventSorts;
import ru.practicum.ewmservice.event.service.EventPublicService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class EventPublicController {
    private final EventPublicService eventPublicService;
    private final HttpServletRequest request;

    @GetMapping
    public List<EventShortDto> findByFilter(@RequestParam(name = "text", defaultValue = "") String text,
                                            @RequestParam(name = "categories", required = false) List<Long> categories,
                                            @RequestParam(name = "paid", required = false) Boolean paid,
                                            @RequestParam(name = "rangeStart", required = false)
                                                LocalDateTime rangeStart,
                                            @RequestParam(name = "rangeEnd", required = false)
                                                LocalDateTime rangeEnd,
                                            @RequestParam(name = "onlyAvailable", defaultValue = "false")
                                                boolean onlyAvailable,
                                            @RequestParam(name = "sort", defaultValue = "EVENT_DATE") EventSorts sort,
                                            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                            @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        return eventPublicService.getAll(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request.getRemoteAddr());
    }

    @GetMapping("/{id}")
    public EventFullDto getById(@PathVariable("id") @Positive long eventId) {
        return eventPublicService.getById(eventId, request.getRemoteAddr());
    }
}
