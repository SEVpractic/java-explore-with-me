package ru.practicum.statsservice.util;

import lombok.experimental.UtilityClass;
import ru.practicum.statsservice.model.App;

@UtilityClass
public class AppMapper {
    public static App toApp(String name) {
        App app = new App();

        app.setName(name);

        return app;
    }
}
