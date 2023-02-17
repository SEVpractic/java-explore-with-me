package ru.yandex.statsservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.statsservice.dto.HitDto;
import ru.yandex.statsservice.dto.Stat;
import ru.yandex.statsservice.service.StatsService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public void save(@Valid @RequestBody HitDto dto) {
        statsService.saveRequest(dto);
    }

    @GetMapping("/stats")
    public List<Stat> get(@RequestParam(name = "start") @NotNull String start,
                          @RequestParam(name = "end") @NotNull String end,
                          @RequestParam(name = "uris")List<String> uris,
                          @RequestParam(name = "unique", defaultValue = "false") boolean unique) {
        return statsService.getHits(start, end, uris, unique);
    }
}
