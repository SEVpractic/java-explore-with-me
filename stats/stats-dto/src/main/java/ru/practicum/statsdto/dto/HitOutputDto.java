package ru.practicum.statsdto.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Getter;
import ru.practicum.statsdto.json.CustomLocalDataSerializer;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Getter
public class HitOutputDto {
    private final Long id;
    private final String app;
    private final String uri;
    private final String ip;
    @JsonProperty("timestamp")
    @JsonSerialize(using = CustomLocalDataSerializer.class)
    private final LocalDateTime timeStamp;

}
