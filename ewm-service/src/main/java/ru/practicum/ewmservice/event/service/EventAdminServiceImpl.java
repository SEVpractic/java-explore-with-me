package ru.practicum.ewmservice.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.event.dto.EventFullDto;
import ru.practicum.ewmservice.event.dto.EventIncomeDto;
import ru.practicum.ewmservice.event.dto.EventSorts;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.model.EventStates;
import ru.practicum.ewmservice.participation_request.model.EventRequest;
import ru.practicum.ewmservice.util.UtilService;
import ru.practicum.ewmservice.util.mappers.EventMapper;
import ru.practicum.ewmservice.util.storage.EventQFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventAdminServiceImpl implements EventAdminService {
    private final UtilService utilService;
    private final EventSuperService eventService;

    @Override
    @Transactional
    public EventFullDto update(EventIncomeDto dto, long eventId) {
        Map<Long, Integer> views;
        List<EventRequest> confirmedRequests;
        eventService.checkEventDate(dto, 1);
        Event event = utilService.findEventOrThrow(eventId);

        event = eventService.update(event, dto);
        confirmedRequests = utilService.findConfirmedRequests(event);
        views = utilService.findViews(eventId);
        log.info("Обновлено событие c id = {} администратором", eventId);

        return EventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    @Override
    public List<EventFullDto> getAll(List<Long> userIds,
                                     List<EventStates> states,
                                     List<Long> categories,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     int from, int size) {
        List<Event> events;
        Map<Long, Integer> views;
        Map<Event, List<EventRequest>> confirmedRequests;

        Pageable pageable = eventService.createPageableBySort(EventSorts.EVENT_DATE, from, size);
        EventQFilter filter = fillFilter(userIds, states, categories, rangeStart, rangeEnd);

        events = utilService.findByFilter(pageable, filter);
        confirmedRequests = utilService.findConfirmedRequests(events);
        views = utilService.findViews(events);

        return EventMapper.toEventFullDto(events, confirmedRequests, views);
    }

    private EventQFilter fillFilter(List<Long> userIds,
                                    List<EventStates> states,
                                    List<Long> categories,
                                    LocalDateTime rangeStart,
                                    LocalDateTime rangeEnd) {
        return EventQFilter.builder()
                .userIds(userIds)
                .states(states == null ? null : states.stream().map(Enum::name).collect(Collectors.toList()))
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .categories(categories)
                .build();
    }
}
