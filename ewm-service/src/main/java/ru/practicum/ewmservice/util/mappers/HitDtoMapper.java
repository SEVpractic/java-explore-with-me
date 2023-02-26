package ru.practicum.ewmservice.util.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.statsdto.HitDto;

import java.time.LocalDateTime;

@UtilityClass
public class HitDtoMapper {
    public static final String APP_NAME = "ewm-main-service";

    public static HitDto fillHit(long eventId, String ip) {
        return HitDto.builder()
                .app(APP_NAME)
                .ip(ip)
                .uri(eventId == 0 ? "/events" : "/events/" + eventId)
                .timeStamp(LocalDateTime.now())
                .build();
    }
}
