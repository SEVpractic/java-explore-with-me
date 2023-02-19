package ru.practicum.statsservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statsdto.HitDto;
import ru.practicum.statsdto.HitOutputDto;
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
    public HitOutputDto save(@Valid @RequestBody HitDto dto) {
        return statsService.saveRequest(dto);
    }

    @GetMapping("/stats")
    public List<Stat> get(@RequestParam(name = "start")
                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                          @RequestParam(name = "end")
                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                          @RequestParam(name = "uris", required = false, defaultValue = "") List<String> uris,
                          @RequestParam(name = "unique", required = false, defaultValue = "false") boolean unique) {
        return statsService.getHits(start, end, uris, unique);
    }
}
