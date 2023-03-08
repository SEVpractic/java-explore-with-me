package ru.practicum.ewmservice.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.event.dto.EventFullDto;
import ru.practicum.ewmservice.event.dto.EventShortDto;
import ru.practicum.ewmservice.event.dto.EventSorts;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.participation_request.model.EventRequest;
import ru.practicum.ewmservice.util.UtilService;
import ru.practicum.ewmservice.util.mappers.EventMapper;
import ru.practicum.ewmservice.util.storage.EventQFilter;
import ru.practicum.statsclient.StatsClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventPublicServiceImpl implements EventPublicService {
    private final StatsClient statsClient;
    private final UtilService utilService;
    private final EventSuperService eventService;

    @Override
    public List<EventShortDto> getAll(String text, List<Long> categories, Boolean paid,
                                      LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      boolean onlyAvailable,
                                      EventSorts sort,
                                      int from, int size,
                                      String ip) {
        List<Event> events;
        Map<Long, Integer> views;
        Map<Event, List<EventRequest>> confirmedRequests;

        EventQFilter filter = fillFilter(text, categories, paid, rangeStart, rangeEnd, onlyAvailable);
        Pageable pageable = eventService.createPageableBySort(sort, from, size);

        events = utilService.findByFilter(pageable, filter);
        log.info("Возвращаю коллекцию событий по запросу");

        confirmedRequests = utilService.findConfirmedRequests(events);
        views = utilService.findViews(events);

        statsClient.saveHits(events.stream().map(Event::getId).collect(Collectors.toList()), ip);
        log.info("Сохраняю в сервере статистики");

        return EventMapper.toEventShortDto(events, confirmedRequests, views);
    }

    @Override
    public EventFullDto getById(long eventId, String ip) {
        Map<Long, Integer> views;
        List<EventRequest> confirmedRequests;

        Event event = utilService.findPublicEventOrThrow(eventId);
        log.info("Возвращаю событие c id = {} ", eventId);

        confirmedRequests = utilService.findConfirmedRequests(event);
        views = utilService.findViews(eventId);

        statsClient.saveHit(eventId, ip);
        log.info("Сохраняю в сервере статистики");

        return EventMapper.toEventFullDto(event, confirmedRequests, views, Map.of());
    }

    private EventQFilter fillFilter(String text, List<Long> categories, Boolean paid,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                    boolean onlyAvailable) {
        return EventQFilter.builder()
                .onlyAvailable(onlyAvailable)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .categories(categories)
                .paid(paid)
                .text("%" + text.trim().toLowerCase() + "%")
                .build();
    }
}
