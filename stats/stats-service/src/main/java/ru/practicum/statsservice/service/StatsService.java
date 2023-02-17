package ru.practicum.statsservice.service;

import ru.practicum.statsdto.dto.HitDto;
import ru.practicum.statsdto.dto.Stat;

import java.util.List;

public interface StatsService {
    public void saveRequest(HitDto dto);

    public List<Stat> getHits(String start, String end, List<String> uris, boolean unique);
}
