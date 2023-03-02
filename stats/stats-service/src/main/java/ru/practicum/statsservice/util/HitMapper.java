package ru.practicum.statsservice.util;

import lombok.experimental.UtilityClass;
import ru.practicum.statsdto.HitDto;
import ru.practicum.statsdto.HitOutputDto;
import ru.practicum.statsdto.HitsDto;
import ru.practicum.statsservice.model.App;
import ru.practicum.statsservice.model.Hit;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class HitMapper {
    public static Hit toHit(HitDto dto, App app) {
        Hit hit = new Hit();

        hit.setApp(app);
        hit.setUri(dto.getUri());
        hit.setIp(dto.getIp());
        hit.setTimeStamp(dto.getTimeStamp());

        return hit;
    }

    public static List<Hit> toHit(HitsDto dto, App app) {
        return dto.getUris().stream()
                .map(uri -> {
                    Hit hit = new Hit();

                    hit.setApp(app);
                    hit.setUri(uri);
                    hit.setIp(dto.getIp());
                    hit.setTimeStamp(dto.getTimeStamp());

                    return hit;
                })
                .collect(Collectors.toList());
    }

    public static HitOutputDto toOutputDto(Hit hit) {
        return HitOutputDto.builder()
                .id(hit.getId())
                .app(hit.getApp().getName())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timeStamp(hit.getTimeStamp())
                .build();
    }
}
