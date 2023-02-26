package ru.practicum.ewmservice.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import ru.practicum.ewmservice.util.validation.CreateValidationGroup;
import ru.practicum.ewmservice.util.validation.UpdateValidationGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventIncomeDto {
    @NotBlank(groups = CreateValidationGroup.class)
    private final String annotation;
    @Positive(groups = CreateValidationGroup.class)
    @JsonProperty("category")
    private final Long categoryId;
    @NotBlank(groups = CreateValidationGroup.class)
    private final String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("eventDate")
    private final LocalDateTime eventDate;
    @NotNull(groups = CreateValidationGroup.class)
    private final LocationDto location;
    @NotNull(groups = CreateValidationGroup.class)
    private final Boolean paid;
    @PositiveOrZero(groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    private final Integer participantLimit;
    @NotNull(groups = CreateValidationGroup.class)
    private final Boolean requestModeration;
    private final StateActions stateAction;
    @NotBlank(groups = CreateValidationGroup.class)
    private final String title;
}
