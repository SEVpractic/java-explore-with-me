package ru.practicum.statsclient;

import kong.unirest.GenericType;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.statsdto.HitDto;
import ru.practicum.statsdto.HitsDto;
import ru.practicum.statsdto.Stat;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StatsClientImpl implements StatsClient {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Value("${stats-server.uri}")
    private String uri;

    @Value("${stat-client.name}")
    private String name;

    @Override
    public void saveHit(long eventId, String ip) {
        HitDto dto = HitDtoMapper.fillHit(eventId, ip, name);
        try {
            saveHit(dto);
        } catch (RuntimeException ex) {
            log.info(ex.getMessage());
        }
    }

    @Override
    public void saveHits(List<Long> eventIds, String ip) {
        HitsDto dto = HitDtoMapper.fillHits(eventIds, ip, name);
        try {
            saveHit(dto);
        } catch (RuntimeException ex) {
            log.info(ex.getMessage());
        }
    }

    @Override
    public List<Stat> getStat(LocalDateTime start, LocalDateTime end, List<Long> eventIds, boolean unique) {
        List<Stat> stats;

        try {
            stats = get(encodeTime(start),
                    encodeTime(end),
                    eventIds.stream().map(e -> "/events/" + e).collect(Collectors.toList()),
                    unique);
        } catch (RuntimeException ex) {
            stats = List.of();
            log.info(ex.getMessage());
        }
        log.info("отправлен запрос на статистику eventIds = {}", eventIds);

        return stats;
    }

    @Override
    public List<Stat> getStat(List<Long> eventIds) {
        List<String> uris = fillUris(eventIds);
        List<Stat> stats = fillStat(uris);
        log.info("отправлен запрос на статистику eventIds = {}", eventIds);
        return stats;
    }

    private String encodeTime(LocalDateTime time) {
        String str = time.format(formatter);
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }

    private List<Stat> get(String start, String end, List<String> uris, boolean unique) {
        return Unirest.get(uri + "/stats")
                .queryString("start", start)
                .queryString("end", end)
                .queryString("uris", uris)
                .queryString("unique", unique)
                .asObject(new GenericType<List<Stat>>(){})
                .getBody();
    }

    private List<Stat> get(List<String> uris) {
        return Unirest.get(uri + "/stats")
                .queryString("uris", uris)
                .asObject(new GenericType<List<Stat>>(){})
                .getBody();
    }

    private void saveHit(HitDto dto) {
        Unirest.post(uri + "/hit")
                .body(dto)
                .contentType("application/json")
                .asEmpty();
        log.info("отправлен запрос на сохранение запроса ip = {} по url = {}", dto.getIp(), dto.getUri());
    }

    private void saveHit(HitsDto dto) {
        Unirest.post(uri + "/hits")
                .body(dto)
                .contentType("application/json")
                .asEmpty();
        log.info("отправлен запрос на сохранение запроса ip = {} по urls = {}", dto.getIp(), dto.getUris());
    }

    private List<String> fillUris(List<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return List.of("/events/");
        } else {
            return eventIds.stream().map(e -> "/events/" + e).collect(Collectors.toList());
        }
    }

    private List<Stat> fillStat(List<String> uris) {
        try {
            return get(uris);
        } catch (RuntimeException ex) {
            log.info(ex.getMessage());
            return List.of();
        }
    }
}
