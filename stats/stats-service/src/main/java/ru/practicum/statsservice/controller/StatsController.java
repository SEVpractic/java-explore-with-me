package ru.practicum.statsservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statsdto.HitDto;
import ru.practicum.statsdto.HitsDto;
import ru.practicum.statsdto.Stat;
import ru.practicum.statsservice.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void save(@Valid @RequestBody HitDto dto) {
        statsService.saveRequest(dto);
    }

    @PostMapping("/hits")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void saveAll(@Valid @RequestBody HitsDto dto) {
        statsService.saveRequest(dto);
    }

    @GetMapping("/stats")
    public List<Stat> get(@RequestParam(name = "start", defaultValue = "1923-01-01 00:00:00")
                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                          @RequestParam(name = "end", defaultValue = "2123-01-01 00:00:00")
                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                          @RequestParam(name = "uris", defaultValue = "") List<String> uris,
                          @RequestParam(name = "unique", defaultValue = "false") boolean unique) {
        return statsService.getHits(start, end, uris, unique);
    }
}
