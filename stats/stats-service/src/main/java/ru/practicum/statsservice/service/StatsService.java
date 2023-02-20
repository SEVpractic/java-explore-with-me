package ru.practicum.statsservice.service;

import ru.practicum.statsdto.HitDto;
import ru.practicum.statsdto.HitOutputDto;
import ru.practicum.statsdto.Stat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    HitOutputDto saveRequest(HitDto dto);

    List<Stat> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
