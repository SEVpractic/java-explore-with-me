package ru.practicum.statsclient;

import ru.practicum.statsdto.Stat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsClient {
    void saveHit(long eventId, String ip);

    void saveHits(List<Long> collect, String ip);

    List<Stat> getStat(LocalDateTime start, LocalDateTime end, List<Long> eventIds, boolean unique);

    List<Stat> getStat(List<Long> eventIds);
}
