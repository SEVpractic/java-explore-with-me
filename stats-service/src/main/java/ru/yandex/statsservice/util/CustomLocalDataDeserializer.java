package ru.yandex.statsservice.util;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomLocalDataDeserializer extends StdDeserializer<LocalDateTime> {

    protected CustomLocalDataDeserializer() {
        this(null);
    }

    protected CustomLocalDataDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext context)
            throws IOException, JacksonException {
        String value = jsonParser.getText();
        if (!"".equals(value)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(value, formatter);
        }
        return null;
    }
}
