package ru.practicum.statsclient;

import ru.practicum.statsdto.HitDto;
import ru.practicum.statsdto.Stat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsClient {
    void saveHit(HitDto dto);

    void saveHit(List<HitDto> dtos);

    List<Stat> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
