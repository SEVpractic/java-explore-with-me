package ru.practicum.ewmservice.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.event.dto.EventFullDto;
import ru.practicum.ewmservice.event.dto.EventShortDto;
import ru.practicum.ewmservice.event.dto.EventSorts;
import ru.practicum.ewmservice.event.service.EventPublicService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class EventPublicController {
    private final EventPublicService eventPublicService;

    @GetMapping
    public List<EventShortDto> findByFilter(@RequestParam(name = "text") String text,
                                            @RequestParam(name = "categories") List<Long> categories,
                                            @RequestParam(name = "paid") boolean paid,
                                            @RequestParam(name = "rangeStart", required = false)
                                                LocalDateTime rangeStart,
                                            @RequestParam(name = "rangeEnd", required = false)
                                                LocalDateTime rangeEnd,
                                            @RequestParam(name = "onlyAvailable", defaultValue = "false")
                                                boolean onlyAvailable,
                                            @RequestParam(name = "sort")EventSorts sort, // todo сделай конвертер
                                            @RequestParam(name = "from", defaultValue = "0") int from,
                                            @RequestParam(name = "size", defaultValue = "10") int size) {
        return eventPublicService.getAll(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{id}")
    public EventFullDto getById(@PathVariable("id") long eventId) {
        return eventPublicService.getById(eventId);
    }
}
