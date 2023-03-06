package ru.practicum.ewmservice.util.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmservice.participation_request.dto.EventRequestDto;
import ru.practicum.ewmservice.participation_request.model.EventRequest;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class EventRequestMapper {
    public static EventRequestDto toEventRequestDto(EventRequest eventRequest) {
        return EventRequestDto.builder()
                .id(eventRequest.getId())
                .created(eventRequest.getCreated())
                .eventId(eventRequest.getEvent().getId())
                .requesterId(eventRequest.getRequester().getId())
                .status(eventRequest.getStatus().getName())
                .build();
    }

    public static List<EventRequestDto> toEventRequestDto(List<EventRequest> eventRequest) {
        return eventRequest.stream()
                .map(EventRequestMapper::toEventRequestDto)
                .collect(Collectors.toList());
    }
}
