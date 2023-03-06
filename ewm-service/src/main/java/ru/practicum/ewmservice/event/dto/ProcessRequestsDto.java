package ru.practicum.ewmservice.event.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.ewmservice.participation_request.dto.EventRequestStats;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder(toBuilder = true)
@Getter
public class ProcessRequestsDto {
    @NotEmpty
    private final List<Long> requestIds;
    @NotNull
    private final EventRequestStats status;
}
