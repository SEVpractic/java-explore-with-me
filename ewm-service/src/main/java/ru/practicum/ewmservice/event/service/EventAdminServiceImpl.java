package ru.practicum.ewmservice.event.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.categories.storage.CategoryRepo;
import ru.practicum.ewmservice.compilation.storage.CompilationRepo;
import ru.practicum.ewmservice.event.dto.EventFullDto;
import ru.practicum.ewmservice.event.dto.EventIncomeDto;
import ru.practicum.ewmservice.event.dto.EventSorts;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.model.EventStates;
import ru.practicum.ewmservice.event.storage.EventRepo;
import ru.practicum.ewmservice.event.storage.EventStateRepo;
import ru.practicum.ewmservice.event.storage.LocationRepo;
import ru.practicum.ewmservice.participation_request.storage.EventRequestRepo;
import ru.practicum.ewmservice.participation_request.storage.EventRequestStatsRepo;
import ru.practicum.ewmservice.user.storage.UserRepo;
import ru.practicum.ewmservice.util.mappers.EventMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class EventAdminServiceImpl extends EventSuperService implements EventAdminService {

    public EventAdminServiceImpl(UserRepo userRepo,
                                 EventRepo eventRepo,
                                 CategoryRepo categoryRepo,
                                 LocationRepo locationRepo,
                                 EventStateRepo eventStateRepo,
                                 CompilationRepo compilationRepo,
                                 EventRequestRepo eventRequestRepo,
                                 EventRequestStatsRepo eventRequestStatsRepo) {
        super(userRepo, eventRepo, categoryRepo, locationRepo, eventStateRepo, compilationRepo,
                eventRequestRepo, eventRequestStatsRepo);
    }

    @Override
    @Transactional
    public EventFullDto update(EventIncomeDto dto, long eventId) {
        Event event = findEventOrThrow(eventId);

        event = update(event, dto);
        log.info("Обновлено событие c id = {} администратором", eventId);

        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventFullDto> getAll(List<Long> userIds,
                                     List<EventStates> states,
                                     List<Long> categories,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     int from, int size) {
        List<Event> events;

        if (states.isEmpty()) {
            states.add(EventStates.PENDING);
            states.add(EventStates.PUBLISHED);
            states.add(EventStates.CANCELED);
        }

        Pageable pageable = createPageableBySort(EventSorts.EVENT_DATE, from, size);
        events = findByUsersAndStates(userIds, states.stream().map(Enum::name).collect(Collectors.toList()),
                categories, rangeStart, rangeEnd, pageable);

        return EventMapper.toEventFullDto(events);
    }

    private List<Event> findByUsersAndStates(List<Long> userIds,
                                             List<String> states,
                                             List<Long> categories,
                                             LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd,
                                             Pageable pageable) {
        List<Event> events;

        if (rangeStart != null && rangeEnd != null) {
            events = findEventsByUsersAndDateRange(userIds, states, categories, rangeStart, rangeEnd, pageable);
            log.info("Сформирован список ивентов по диапазону дат с = {} по = {}", rangeStart, rangeEnd);
        } else if (rangeStart != null) {
            events = findEventsByUsersAndStartDate(userIds, states, categories, rangeStart, pageable);
            log.info("Сформирован список ивентов по диапазону дат с = {}", rangeStart);
        } else {
            events = findEventsByUsersFromNow(userIds, states, categories, pageable);
            log.info("Сформирован список ивентов по диапазону дат от текущего момента");
        }

        return events;
    }

    private List<Event> findEventsByUsersAndDateRange(List<Long> userIds, List<String> states, List<Long> categories,
                                                      LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                      Pageable pageable) {
        return eventRepo.findEventsByUsersAndDateRange(userIds, states, categories, rangeStart, rangeEnd, pageable);
    }

    private List<Event> findEventsByUsersAndStartDate(List<Long> userIds, List<String> states,
                                                      List<Long> categories, LocalDateTime rangeStart,
                                                      Pageable pageable) {
        return eventRepo.findEventsByUsersStartDate(userIds, states, categories, rangeStart, pageable);
    }

    private List<Event> findEventsByUsersFromNow(List<Long> userIds, List<String> states,
                                                 List<Long> categories, Pageable pageable) {
        return eventRepo.findEventsByUsers(userIds, states, categories, pageable);
    }
}
