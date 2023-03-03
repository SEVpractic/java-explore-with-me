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
import ru.practicum.ewmservice.event.storage.EventRepo;
import ru.practicum.ewmservice.participation_request.model.EventRequest;
import ru.practicum.ewmservice.util.UtilService;
import ru.practicum.ewmservice.util.mappers.EventMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventAdminServiceImpl implements EventAdminService {
    private final EventRepo eventRepo;
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
        events = findByUsersAndStates(userIds, checkSates(states), categories, rangeStart, rangeEnd, pageable);
        confirmedRequests = utilService.findConfirmedRequests(events);
        views = utilService.findViews(events);

        return EventMapper.toEventFullDto(events, confirmedRequests, views);
    }

    private List<Event> findByUsersAndStates(List<Long> userIds,
                                             List<String> states,
                                             List<Long> categories,
                                             LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd,
                                             Pageable pageable) {
        List<Event> events;

        if (rangeStart != null && rangeEnd != null) {
            events = findAllByDateRange(userIds, states, categories, rangeStart, rangeEnd, pageable);
            log.info("Сформирован список ивентов по диапазону дат с = {} по = {}", rangeStart, rangeEnd);
        } else if (rangeStart != null) {
            events = findAllByStartDate(userIds, states, categories, rangeStart, pageable);
            log.info("Сформирован список ивентов по диапазону дат с = {}", rangeStart);
        } else {
            events = findAllFromNow(userIds, states, categories, pageable);
            log.info("Сформирован список ивентов по диапазону дат от текущего момента");
        }

        return events;
    }

    private List<Event> findAllByDateRange(List<Long> userIds, List<String> states, List<Long> categories,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                           Pageable pageable) {
        if ((userIds == null || userIds.isEmpty())
                && (categories == null || categories.isEmpty())) {
            return eventRepo.findAllByDateRange(states, rangeStart, rangeEnd, pageable);
        } else if (userIds == null || userIds.isEmpty()) {
            return eventRepo.findAllByCategoriesAndDateRange(states, categories, rangeStart, rangeEnd, pageable);
        } else if (categories == null || categories.isEmpty()) {
            return eventRepo.findAllByUsersAndDateRange(userIds, states, rangeStart, rangeEnd, pageable);
        } else {
            return eventRepo.findAllByUsersAndCategoriesAndDateRange(userIds, states, categories,
                    rangeStart, rangeEnd, pageable);
        }
    }

    private List<Event> findAllByStartDate(List<Long> userIds, List<String> states,
                                           List<Long> categories, LocalDateTime rangeStart,
                                           Pageable pageable) {
        if ((userIds == null || userIds.isEmpty())
                && (categories == null || categories.isEmpty())) {
            return eventRepo.findAllByStartDate(states, rangeStart, pageable);
        } else if (userIds == null || userIds.isEmpty()) {
            return eventRepo.findAllByCategoriesAndStartDate(states, categories, rangeStart, pageable);
        } else if (categories == null || categories.isEmpty()) {
            return eventRepo.findAllByUsersAndStartDate(userIds, states, rangeStart, pageable);
        } else {
            return eventRepo.findAllByUsersAndCategoriesAndStartDate(userIds, states, categories, rangeStart, pageable);
        }
    }

    private List<Event> findAllFromNow(List<Long> userIds, List<String> states,
                                       List<Long> categories, Pageable pageable) {
        if ((userIds == null || userIds.isEmpty())
                && (categories == null || categories.isEmpty())) {
            return eventRepo.findAllByNow(states, pageable);
        } else if (userIds == null || userIds.isEmpty()) {
            return eventRepo.findAllByCategories(states, categories, pageable);
        } else if (categories == null || categories.isEmpty()) {
            return eventRepo.findAllByUsers(userIds, states, pageable);
        } else {
            return eventRepo.findAllByUsersAndCategories(userIds, states, categories, pageable);
        }
    }

    private List<String> checkSates(List<EventStates> states) {
        if (states == null || states.isEmpty()) {
            return List.of(
                    EventStates.PENDING.name(),
                    EventStates.PUBLISHED.name(),
                    EventStates.CANCELED.name());
        }
        return states.stream().map(Enum::name).collect(Collectors.toList());
     }
}
