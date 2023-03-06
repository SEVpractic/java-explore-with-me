package ru.practicum.ewmservice.event.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Builder(toBuilder = true)
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventIncomeShortDto { //todo DELETE!!!
    @NotNull
    @Positive
    @JsonProperty("id")
    private final Long eventId;
    @NotNull
    private final StateActions stateAction;
    private final String comment;
}
