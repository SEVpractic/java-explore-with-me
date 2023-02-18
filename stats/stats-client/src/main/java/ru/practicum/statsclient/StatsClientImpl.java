package ru.practicum.statsclient;

import kong.unirest.GenericType;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.statsdto.dto.HitDto;
import ru.practicum.statsdto.dto.Stat;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsClientImpl implements StatsClient {
    @Value("${stats-client.uri}")
    private final String uri;

    @Override
    public void saveHit(HitDto dto) {
        Unirest.post(uri + "/hit").body(dto);
        log.info("отправлен запрос на сохранение запроса ip = {} по url = {}", dto.getIp(), dto.getUri());
    }

    @Override
    public List<Stat> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("отправлен запрос на статистику uris = {}", uris);
        return get(encodeTime(start), encodeTime(end), uris, unique);
    }

    private String encodeTime(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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
