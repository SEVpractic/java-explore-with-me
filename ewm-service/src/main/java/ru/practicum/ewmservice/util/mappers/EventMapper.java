package ru.practicum.ewmservice.util.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmservice.categories.model.Category;
import ru.practicum.ewmservice.event.dto.EventFullDto;
import ru.practicum.ewmservice.event.dto.EventIncomeDto;
import ru.practicum.ewmservice.event.dto.EventShortDto;
import ru.practicum.ewmservice.event.model.AdminComment;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.model.Location;
import ru.practicum.ewmservice.participation_request.model.EventRequest;
import ru.practicum.ewmservice.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class EventMapper {
    public static Event toEvent(EventIncomeDto dto, Category category, Location location, User user) {
        Event event = new Event();

        event.setAnnotation(dto.getAnnotation());
        event.setCategory(category);
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setInitiator(user);
        event.setLocation(location);
        event.setPaid(dto.getPaid());
        event.setParticipantLimit(dto.getParticipantLimit());
        event.setRequestModeration(dto.getRequestModeration());
        event.setTitle(dto.getTitle());

        return event;
    }

    public static EventFullDto toEventFullDto(Event event,
                                              List<EventRequest> confirmedRequests,
                                              Map<Long, Integer> views,
                                              Map<Long, AdminComment> comments) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests.size())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserDto(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState().getName())
                .title(event.getTitle())
                .views(views.getOrDefault(event.getId(), 0))
                .comment(
                        comments.containsKey(event.getId()) ?
                                AdminCommentMapper.toAdminCommentDto(comments.get(event.getId())) : null
                )
                .build();
    }

    public static List<EventFullDto> toEventFullDto(List<Event> events,
                                                    Map<Event, List<EventRequest>> confirmedRequests,
                                                    Map<Long, Integer> views,
                                                    Map<Long, AdminComment> comments) {
        return events.stream()
                .map(event -> toEventFullDto(
                        event,
                        confirmedRequests.getOrDefault(event, List.of()),
                        views,
                        comments
                ))
                .collect(Collectors.toList());
    }

    public static EventShortDto toEventShortDto(Event event,
                                                List<EventRequest> confirmedRequests,
                                                Map<Long, Integer> views) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests.size())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(views.getOrDefault(event.getId(), 0))
                .build();
    }

    public static List<EventShortDto> toEventShortDto(List<Event> events,
                                                      Map<Event, List<EventRequest>> confirmedRequests,
                                                      Map<Long, Integer> views) {
        return events.stream()
                .map(event -> toEventShortDto(
                        event,
                        confirmedRequests.getOrDefault(event, List.of()),
                        views
                ))
                .collect(Collectors.toList());
    }
}
