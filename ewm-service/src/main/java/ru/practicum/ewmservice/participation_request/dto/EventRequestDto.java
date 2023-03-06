package ru.practicum.ewmservice.participation_request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Getter
public class EventRequestDto {
    private final Long id;
    private final LocalDateTime created;
    @JsonProperty("event")
    private final Long eventId;
    @JsonProperty("requester")
    private final Long requesterId;
    private final String status;
}
