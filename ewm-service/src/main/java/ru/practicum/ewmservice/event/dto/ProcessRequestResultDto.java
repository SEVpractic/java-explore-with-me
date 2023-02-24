package ru.practicum.ewmservice.event.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.ewmservice.participation_request.dto.EventRequestDto;

import java.util.List;

@Builder(toBuilder = true)
@Getter
public class ProcessRequestResultDto {
    private final List<EventRequestDto> confirmedRequests;
    private final List<EventRequestDto> rejectedRequests;
}
