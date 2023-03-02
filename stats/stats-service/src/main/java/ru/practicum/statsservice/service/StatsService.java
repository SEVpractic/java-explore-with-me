package ru.practicum.statsservice.service;

import ru.practicum.statsdto.HitDto;
import ru.practicum.statsdto.HitsDto;
import ru.practicum.statsdto.Stat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void saveRequest(HitDto dto);

    void saveRequest(HitsDto dto);

    List<Stat> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
