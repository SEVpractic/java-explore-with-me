package ru.practicum.ewmservice.compilation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import ru.practicum.ewmservice.util.validation.CreateValidationGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder(toBuilder = true)
@Getter
public class CompilationIncomeDto {
    @JsonProperty("events")
    private final List<Long> eventIds;
    @NotNull(groups = CreateValidationGroup.class)
    private final Boolean pinned;
    @NotBlank(groups = CreateValidationGroup.class)
    private final String title;
}
