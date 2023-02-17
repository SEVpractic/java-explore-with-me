package ru.yandex.statsservice.util;

import lombok.experimental.UtilityClass;
import ru.yandex.statsservice.dto.HitDto;
import ru.yandex.statsservice.model.Hit;

@UtilityClass
public class HitMapper {
    public static Hit toHit(HitDto dto) {
        Hit hit = new Hit();

        hit.setApp(dto.getApp());
        hit.setUri(dto.getUri());
        hit.setIp(dto.getIp());
        hit.setTimeStamp(dto.getTimeStamp());

        return hit;
    }
}
