package ru.yandex.statsservice.service;

import ru.yandex.statsservice.dto.HitDto;
import ru.yandex.statsservice.dto.Stat;

import java.util.List;

public interface StatsService {
    public void saveRequest(HitDto dto);

    public List<Stat> getHits(String start, String end, List<String> uris, boolean unique);
}
