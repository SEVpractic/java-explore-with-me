package ru.practicum.statsdto.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomLocalDataSerializer extends StdSerializer<LocalDateTime> {

    protected CustomLocalDataSerializer(Class<LocalDateTime> t) {
        super(t);
    }

    protected CustomLocalDataSerializer() {
        this(null);
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        gen.writeString(formatter.format(value));
    }
}
