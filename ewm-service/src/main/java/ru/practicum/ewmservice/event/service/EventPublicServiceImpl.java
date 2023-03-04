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
import ru.practicum.ewmservice.event.storage.EventRepo;
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
    private final EventRepo eventRepo;
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

        EventQFilter filter = fillFilter(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, ip);
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

        return EventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    private List<Event> findByText(String text, List<Long> categories, Boolean paid,
                                   LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                   boolean onlyAvailable, Pageable pageable) {
        List<Event> events;
        text = text.trim().toLowerCase();

        if (rangeStart != null && rangeEnd != null) {
            events = findEventsByDateRange(text, categories, paid, rangeStart,
                    rangeEnd, onlyAvailable, pageable);
            log.info("Сформирован список опубликованных ивентов по диапазону дат с = {} по = {}", rangeStart, rangeEnd);
        } else if (rangeStart != null) {
            events = findEventsByStartDate(text, categories, paid, rangeStart,
                    onlyAvailable, pageable);
            log.info("Сформирован список опубликованных ивентов по диапазону дат с = {}", rangeStart);
        } else {
            events = findEventsFromNow(text, categories, paid, onlyAvailable, pageable);
            log.info("Сформирован список опубликованных ивентов по диапазону дат от текущего момента");
        }

        return events;
    }

    private List<Event> findEventsByDateRange(String text, List<Long> categories, Boolean paid,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                              boolean onlyAvailable, Pageable pageable) {
        if (onlyAvailable) {
            if ((categories == null || categories.isEmpty()) && paid == null) {
                return eventRepo.findAvailableEventsByDateRange(text, rangeStart, rangeEnd, pageable);
            } else if (categories == null || categories.isEmpty()) {
                return eventRepo.findAvailableEventsByDateRange(text, paid, rangeStart, rangeEnd, pageable);
            } else if (paid == null) {
                return eventRepo.findAvailableEventsByDateRange(text, categories, rangeStart, rangeEnd, pageable);
            } else {
                return eventRepo.findAvailableEventsByDateRange(text, categories, paid, rangeStart, rangeEnd, pageable);
            }
        } else {
            if ((categories == null || categories.isEmpty()) && paid == null) {
                return eventRepo.findEventsByDateRange(text, rangeStart, rangeEnd, pageable);
            } else if (categories == null || categories.isEmpty()) {
                return eventRepo.findEventsByDateRange(text, paid, rangeStart, rangeEnd, pageable);
            } else if (paid == null) {
                return eventRepo.findEventsByDateRange(text, categories, rangeStart, rangeEnd, pageable);
            } else {
                return eventRepo.findEventsByDateRange(text, categories, paid, rangeStart, rangeEnd, pageable);
            }
        }
    }

    private List<Event> findEventsByStartDate(String text, List<Long> categories, Boolean paid,
                                              LocalDateTime rangeStart, boolean onlyAvailable,
                                              Pageable pageable) {
        if (onlyAvailable) {
            if ((categories == null || categories.isEmpty()) && paid == null) {
                return eventRepo.findAvailableEventsByStartDate(text, rangeStart, pageable);
            } else if (categories == null || categories.isEmpty()) {
                return eventRepo.findAvailableEventsByStartDate(text, paid, rangeStart, pageable);
            } else if (paid == null) {
                return eventRepo.findAvailableEventsByStartDate(text, categories, rangeStart, pageable);
            } else {
                return eventRepo.findAvailableEventsByStartDate(text, categories, paid, rangeStart, pageable);
            }
        } else {
            if ((categories == null || categories.isEmpty()) && paid == null) {
                return eventRepo.findEventsByStartDate(text, rangeStart, pageable);
            } else if (categories == null || categories.isEmpty()) {
                return eventRepo.findEventsByStartDate(text, paid, rangeStart, pageable);
            } else if (paid == null) {
                return eventRepo.findEventsByStartDate(text, categories, rangeStart, pageable);
            } else {
                return eventRepo.findEventsByStartDate(text, categories, paid, rangeStart, pageable);
            }
        }
    }

    private List<Event> findEventsFromNow(String text, List<Long> categories, Boolean paid,
                                          boolean onlyAvailable, Pageable pageable) {
        if (onlyAvailable) {
            if ((categories == null || categories.isEmpty()) && paid == null) {
                return eventRepo.findAvailableEventsFromNow(text, pageable);
            } else if (categories == null || categories.isEmpty()) {
                return eventRepo.findAvailableEventsFromNow(text, paid, pageable);
            } else if (paid == null) {
                return eventRepo.findAvailableEventsFromNow(text, categories, pageable);
            } else {
                return eventRepo.findAvailableEventsFromNow(text, categories, paid, pageable);
            }
        } else {
            if ((categories == null || categories.isEmpty()) && paid == null) {
                return eventRepo.findEventsFromNow(text, pageable);
            } else if (categories == null || categories.isEmpty()) {
                return eventRepo.findEventsFromNow(text, paid, pageable);
            } else if (paid == null) {
                return eventRepo.findEventsFromNow(text, categories, pageable);
            } else {
                return eventRepo.findEventsFromNow(text, categories, paid, pageable);
            }
        }
    }

    private EventQFilter fillFilter(String text, List<Long> categories, Boolean paid,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                    boolean onlyAvailable, String ip) {
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
