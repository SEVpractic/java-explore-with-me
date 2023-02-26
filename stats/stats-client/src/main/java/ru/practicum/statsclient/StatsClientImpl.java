package ru.practicum.statsclient;

import kong.unirest.GenericType;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.statsdto.HitDto;
import ru.practicum.statsdto.Stat;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class StatsClientImpl implements StatsClient {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String uri = "http://stats-server:9090";

    @Override
    public void saveHit(HitDto dto) {
        Unirest.post(uri + "/hit")
                .body(dto)
                .contentType("application/json")
                .asEmpty();
        log.info("отправлен запрос на сохранение запроса ip = {} по url = {}", dto.getIp(), dto.getUri());
    }

    @Override
    public void saveHit(List<HitDto> dtos) {
        Unirest.post(uri + "/hit")
                .body(dtos)
                .contentType("application/json")
                .asEmpty();
    }

    @Override
    public List<Stat> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("отправлен запрос на статистику uris = {}", uris);
        return get(encodeTime(start), encodeTime(end), uris, unique);
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
}
