package ru.practicum.ewmservice.event.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.categories.storage.CategoryRepo;
import ru.practicum.ewmservice.compilation.storage.CompilationRepo;
import ru.practicum.ewmservice.event.dto.EventFullDto;
import ru.practicum.ewmservice.event.dto.EventShortDto;
import ru.practicum.ewmservice.event.dto.EventSorts;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.storage.EventRepo;
import ru.practicum.ewmservice.event.storage.EventStateRepo;
import ru.practicum.ewmservice.event.storage.LocationRepo;
import ru.practicum.ewmservice.participation_request.storage.EventRequestRepo;
import ru.practicum.ewmservice.participation_request.storage.EventRequestStatsRepo;
import ru.practicum.ewmservice.user.storage.UserRepo;
import ru.practicum.ewmservice.util.mappers.EventMapper;
import ru.practicum.statsclient.StatsClientImpl;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional
public class EventPublicServiceImpl extends EventSuperService implements EventPublicService {
    public EventPublicServiceImpl(UserRepo userRepo,
                                  EventRepo eventRepo,
                                  CategoryRepo categoryRepo,
                                  LocationRepo locationRepo,
                                  EventStateRepo eventStateRepo,
                                  CompilationRepo compilationRepo,
                                  EventRequestRepo eventRequestRepo,
                                  EventRequestStatsRepo eventRequestStatsRepo,
                                  StatsClientImpl statsClient) {
        super(userRepo, eventRepo, categoryRepo, locationRepo, eventStateRepo,
                compilationRepo, eventRequestRepo, eventRequestStatsRepo, statsClient);
    }

    @Override
    public List<EventShortDto> getAll(String text, List<Long> categories, boolean paid,
                                      LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      boolean onlyAvailable,
                                      EventSorts sort,
                                      int from, int size,
                                      String ip) {
        List<Event> events;

        Pageable pageable = createPageableBySort(sort, from, size);
        events = findByText(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable);
        log.info("Возвращаю коллекцию событий по запросу");

        saveStat(0, ip);
        log.info("Сохраняю в сервере статистики");

        return EventMapper.toEventShortDto(events);
    }

    @Override
    public EventFullDto getById(long eventId, String ip) {
        Event event = findPublicEventOrThrow(eventId);
        log.info("Возвращаю событие c id = {} ", eventId);

        saveStat(eventId, ip);
        event.setViews(event.getViews() + 1);
        log.info("Сохраняю в сервере статистики");

        return EventMapper.toEventFullDto(event);
    }

    private List<Event> findByText(String text, List<Long> categories, boolean paid,
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

    private List<Event> findEventsByDateRange(String text, List<Long> categories, boolean paid,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                              boolean onlyAvailable, Pageable pageable) {
        if (onlyAvailable) {
            return eventRepo.findAvailableEventsByDateRange(text, categories, paid, rangeStart, rangeEnd, pageable);
        } else {
            return eventRepo.findEventsByDateRange(text, categories, paid, rangeStart, rangeEnd, pageable);
        }
    }

    private List<Event> findEventsByStartDate(String text, List<Long> categories, boolean paid,
                                              LocalDateTime rangeStart, boolean onlyAvailable,
                                              Pageable pageable) {
        if (onlyAvailable) {
            return eventRepo.findAvailableEventsByStartDate(text, categories, paid, rangeStart, pageable);
        } else {
            return eventRepo.findEventsByStartDate(text, categories, paid, rangeStart, pageable);
        }
    }

    private List<Event> findEventsFromNow(String text, List<Long> categories, boolean paid,
                                          boolean onlyAvailable, Pageable pageable) {
        if (onlyAvailable) {
            return eventRepo.findAvailableEventsFromNow(text, categories, paid, pageable);
        } else {
            return eventRepo.findEventsFromNow(text, categories, paid, pageable);
        }
    }
}
