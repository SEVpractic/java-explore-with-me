package ru.practicum.statsclient;

import lombok.experimental.UtilityClass;
import ru.practicum.statsdto.HitDto;
import ru.practicum.statsdto.HitsDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class HitDtoMapper {
    public static HitDto fillHit(long eventId, String ip, String name) {
        return HitDto.builder()
                .app(name)
                .ip(ip)
                .uri("/events/" + eventId)
                .timeStamp(LocalDateTime.now())
                .build();
    }


    public static HitsDto fillHits(List<Long> eventIds, String ip, String name) {
        List<String> uris = eventIds.stream().map(id -> "/events/" + id).collect(Collectors.toList());
        uris.add("/events");
        return HitsDto.builder()
                .app(name)
                .ip(ip)
                .uris(uris)
                .timeStamp(LocalDateTime.now())
                .build();
    }
}
