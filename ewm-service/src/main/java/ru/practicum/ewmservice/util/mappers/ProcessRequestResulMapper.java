package ru.practicum.ewmservice.util.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmservice.event.dto.ProcessRequestResultDto;
import ru.practicum.ewmservice.participation_request.dto.EventRequestDto;
import ru.practicum.ewmservice.participation_request.dto.EventRequestStats;
import ru.practicum.ewmservice.participation_request.model.EventRequest;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ProcessRequestResulMapper {
    public static ProcessRequestResultDto toDto(List<EventRequest> requests) {
        List<EventRequestDto> mappedRequests = EventRequestMapper.toEventRequestDto(requests);

        return ProcessRequestResultDto.builder()
                .confirmedRequests(
                        mappedRequests.stream()
                                .filter(r -> r.getStatus().equals(EventRequestStats.CONFIRMED.name()))
                                .collect(Collectors.toList())
                )
                .rejectedRequests(
                        mappedRequests.stream()
                                .filter(r -> r.getStatus().equals(EventRequestStats.REJECTED.name()))
                                .collect(Collectors.toList())
                )
                .build();
    }
}
