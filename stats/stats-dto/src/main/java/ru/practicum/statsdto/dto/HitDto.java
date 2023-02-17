package ru.practicum.statsdto.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Getter;
import ru.practicum.statsdto.json.CustomLocalDataDeserializer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Getter
public class HitDto {
    @NotBlank
    private final String app;
    @NotBlank
    private final String uri;
    @NotBlank
    private final String ip;
    @NotNull
    @JsonProperty("timestamp")
    @JsonDeserialize(using = CustomLocalDataDeserializer.class)
    private final LocalDateTime timeStamp;
}
