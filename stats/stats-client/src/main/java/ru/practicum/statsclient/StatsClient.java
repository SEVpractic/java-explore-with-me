package ru.practicum.statsclient;

import ru.practicum.statsdto.HitDto;
import ru.practicum.statsdto.Stat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsClient {
    void saveHit(HitDto dto);

    List<Stat> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
