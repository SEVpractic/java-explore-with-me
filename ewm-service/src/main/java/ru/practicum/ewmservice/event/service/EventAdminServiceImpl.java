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
import ru.practicum.ewmservice.event.model.AdminComment;
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

import static java.util.stream.Collectors.toMap;

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
        Map<Long, AdminComment> comments;
        eventService.checkEventDate(dto, 1);
        Event event = utilService.findEventOrThrow(eventId);

        event = eventService.update(event, dto);
        confirmedRequests = utilService.findConfirmedRequests(event);
        views = utilService.findViews(eventId);

        comments = eventService.saveAdminComment(List.of(dto), List.of(event));

        log.info("Обновлено событие c id = {} администратором", eventId);
        return EventMapper.toEventFullDto(event, confirmedRequests, views, comments); // todo map!
    }

    @Override
    @Transactional
    public List<EventFullDto> updateAll(List<EventIncomeDto> dto) {
        Map<Long, AdminComment> comments;
        List<Event> events = getAll(dto.stream().map(EventIncomeDto::getEventId).collect(Collectors.toList()),
                null, null, null, null, null, 0, dto.size());
        Map<Long, Integer> views = utilService.findViews(events);
        Map<Event, List<EventRequest>> confirmedRequests = utilService.findConfirmedRequests(events);

        Map<Long, EventIncomeDto> dtoMap = dto.stream().collect(toMap(EventIncomeDto::getEventId, i -> i));
        events.forEach(event -> eventService.update(event, dtoMap.get(event.getId())));

        comments = eventService.saveAdminComment(dto, events);

        log.info("Обновлен перечень событий администратором");
        return EventMapper.toEventFullDto(events, confirmedRequests, views, comments); // todo map!
    }

    @Override
    public List<EventFullDto> getAll(List<Long> userIds,
                                     List<EventStates> states,
                                     List<Long> categories,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     int from, int size) {
        List<Event> events = getAll(null, userIds, states, categories, rangeStart, rangeEnd, from, size);
        Map<Long, Integer> views = utilService.findViews(events);
        Map<Event, List<EventRequest>> confirmedRequests = utilService.findConfirmedRequests(events);
        Map<Long, AdminComment> comments = utilService.findByEventId(events);

        log.info("Возвращаю список событий по запросу администратора");
        return EventMapper.toEventFullDto(events, confirmedRequests, views, comments); // todo map!
    }

    @Override
    public List<EventFullDto> getWaiting(int from, int size) { // todo убрать метод, переделать запрос из контроллера
        List<Event> events = getAll(null, null, List.of(EventStates.PENDING),
                null, null, null, from, size);
        Map<Long, Integer> views = utilService.findViews(events);
        Map<Event, List<EventRequest>> confirmedRequests = utilService.findConfirmedRequests(events);

        log.info("Возвращаю список событий по запросу администратора");
        return EventMapper.toEventFullDto(events, confirmedRequests, views, Map.of());
    }

    private List<Event> getAll(List<Long> eventIds,
                               List<Long> userIds,
                               List<EventStates> states,
                               List<Long> categories,
                               LocalDateTime rangeStart,
                               LocalDateTime rangeEnd,
                               int from, int size) {
        List<Event> events;

        Pageable pageable = eventService.createPageableBySort(EventSorts.EVENT_DATE, from, size);
        EventQFilter filter = fillFilter(userIds, states, categories, rangeStart, rangeEnd, eventIds);

        events = utilService.findByFilter(pageable, filter);

        return events;
    }

    private EventQFilter fillFilter(List<Long> userIds,
                                    List<EventStates> states,
                                    List<Long> categories,
                                    LocalDateTime rangeStart,
                                    LocalDateTime rangeEnd,
                                    List<Long> eventIds) {
        return EventQFilter.builder()
                .userIds(userIds)
                .states(states == null ? null : states.stream().map(Enum::name).collect(Collectors.toList()))
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .categories(categories)
                .eventIds(eventIds)
                .build();
    }
}
