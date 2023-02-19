package ru.practicum.statsservice.util;

import lombok.experimental.UtilityClass;
import ru.practicum.statsdto.HitDto;
import ru.practicum.statsservice.model.App;

@UtilityClass
public class AppMapper {
    public static App toApp(HitDto dto) {
        App app = new App();

        app.setName(dto.getApp());

        return app;
    }
}
