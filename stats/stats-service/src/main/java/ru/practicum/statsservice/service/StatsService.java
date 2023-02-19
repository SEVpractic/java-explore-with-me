package ru.practicum.statsservice.service;

import ru.practicum.statsdto.dto.HitDto;
import ru.practicum.statsdto.dto.HitOutputDto;
import ru.practicum.statsdto.dto.Stat;

import java.util.List;

public interface StatsService {
    HitOutputDto saveRequest(HitDto dto);

    List<Stat> getHits(String start, String end, List<String> uris, boolean unique);
}
