package ru.practicum.ewmservice.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.categories.model.Category;
import ru.practicum.ewmservice.event.dto.*;
import ru.practicum.ewmservice.event.model.AdminComment;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.model.EventStates;
import ru.practicum.ewmservice.event.model.Location;
import ru.practicum.ewmservice.event.storage.EventRepo;
import ru.practicum.ewmservice.participation_request.dto.EventRequestDto;
import ru.practicum.ewmservice.participation_request.dto.EventRequestStats;
import ru.practicum.ewmservice.participation_request.model.EventRequest;
import ru.practicum.ewmservice.participation_request.model.EventRequestStat;
import ru.practicum.ewmservice.participation_request.storage.EventRequestRepo;
import ru.practicum.ewmservice.user.model.User;
import ru.practicum.ewmservice.util.UtilService;
import ru.practicum.ewmservice.util.exceptions.OperationFailedException;
import ru.practicum.ewmservice.util.mappers.EventMapper;
import ru.practicum.ewmservice.util.mappers.EventRequestMapper;
import ru.practicum.ewmservice.util.mappers.LocationMapper;
import ru.practicum.ewmservice.util.mappers.ProcessRequestResulMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventPrivateServiceImpl implements EventPrivateService {
    private final EventRepo eventRepo;
    private final UtilService utilService;
    private final EventSuperService eventService;
    private final EventRequestRepo eventRequestRepo;

    @Override
    @Transactional
    public EventFullDto create(EventIncomeDto dto, long userId) {
        eventService.checkEventDate(dto, 2);
        User initiator = utilService.findUserOrThrow(userId);
        Category category = utilService.findCategoryOrThrow(dto.getCategoryId());
        Location location = eventService.findLocationOrSave(LocationMapper.toLocation(dto));
        Event event = EventMapper.toEvent(dto, category, location, initiator);

        event.setState(utilService.findEventStateOrThrow(EventStates.PENDING));
        event.setCreatedOn(LocalDateTime.now());
        event = eventRepo.save(event);
        log.info("Создано событие c id = {} ", event.getId());

        return EventMapper.toEventFullDto(event, List.of(), Map.of(), Map.of());
    }

    @Override
    @Transactional
    public EventFullDto update(EventIncomeDto dto, long userId, long eventId) {
        Map<Long, Integer> views;
        List<EventRequest> confirmedRequests;
        Map<Long, AdminComment> comments;
        eventService.checkEventDate(dto, 2);
        User user = utilService.findUserOrThrow(userId);
        Event event = utilService.findEventOrThrow(eventId);

        checkInitiator(user, event);
        checkUpdateAvailable(event);

        event = eventService.update(event, dto);
        confirmedRequests = utilService.findConfirmedRequests(event);
        views = utilService.findViews(eventId);
        comments = utilService.findByEventId(List.of(event));
        log.info("Обновлено событие c id = {} юзером с id = {}", eventId, userId);

        return EventMapper.toEventFullDto(event, confirmedRequests, views, comments); // todo map!
    }

    @Override
    public List<EventShortDto> getAll(long userId) {
        Map<Long, Integer> views;
        Map<Event, List<EventRequest>> confirmedRequests;
        User initiator = utilService.findUserOrThrow(userId);

        List<Event> events = eventRepo.findAllByInitiator(initiator);
        confirmedRequests = utilService.findConfirmedRequests(events);
        views = utilService.findViews(events);
        log.info("Сформирован список ивентов пользователя c id = {} ", initiator);

        return EventMapper.toEventShortDto(events, confirmedRequests, views);
    }

    @Override
    public EventFullDto getById(long userId, long eventId) {
        Map<Long, Integer> views;
        List<EventRequest> confirmedRequests;
        Map<Long, AdminComment> comments;
        utilService.findUserOrThrow(userId);

        Event event = utilService.findEventOrThrow(eventId);
        confirmedRequests = utilService.findConfirmedRequests(event);
        views = utilService.findViews(eventId);
        comments = utilService.findByEventId(List.of(event));

        log.info("Возвращаю событие c id = {} ", eventId);

        return EventMapper.toEventFullDto(event, confirmedRequests, views, comments); // todo map!
    }

    @Override
    public List<EventRequestDto> getRequests(long userId, long eventId) {
        List<EventRequest> requests;
        User user = utilService.findUserOrThrow(userId);
        Event event = utilService.findEventOrThrow(eventId);
        checkInitiator(user, event);

        requests = findEventRequestsByEvent(event);
        log.info("Возвращаю перечень запросов на участие в событии c id = {} ", eventId);

        return EventRequestMapper.toEventRequestDto(requests);
    }

    @Override
    @Transactional
    public ProcessRequestResultDto processRequests(long userId, long eventId, ProcessRequestsDto dto) {
        User user = utilService.findUserOrThrow(userId);
        Event event = utilService.findEventOrThrow(eventId);
        checkInitiator(user, event);

        List<EventRequest> requests = findEventRequestsByIds(dto.getRequestIds());
        checkProcessRequestsAvailable(
                event,
                utilService.findConfirmedRequests(event)
        );
        if (requests.isEmpty()) return ProcessRequestResultDto.builder().build();
        requests = filterRequestsByStat(requests);

        switch (dto.getStatus()) {
            case REJECTED:
                rejectRequests(requests);
                break;
            case CONFIRMED:
                filterAndProcessRequests(requests, event);
                break;
        }

        return ProcessRequestResulMapper.toDto(requests);
    }

    private void filterAndProcessRequests(List<EventRequest> requests, Event event) {
        if (event.getParticipantLimit() == 0) {
            confirmRequests(requests);
        } else {
            int limit = event.getParticipantLimit() - utilService.findConfirmedRequests(event).size();

            if (requests.size() <= limit) {
                confirmRequests(requests);
            } else if (limit > 0) {
                confirmRequests(requests.subList(0, limit - 1));
                rejectRequests(requests.subList(limit, requests.size()));
            } else {
                rejectRequests(requests);
            }
        }
    }

    private void confirmRequests(List<EventRequest> requests) {
        EventRequestStat stat = utilService.findRequestStatOrThrow(EventRequestStats.CONFIRMED);
        requests.forEach(request -> request.setStatus(stat));
    }

    private void rejectRequests(List<EventRequest> requests) {
        EventRequestStat stat = utilService.findRequestStatOrThrow(EventRequestStats.REJECTED);
        requests.forEach(request -> request.setStatus(stat));
    }

    private List<EventRequest> filterRequestsByStat(List<EventRequest> requests) {
        return requests.stream()
                .filter(r -> r.getStatus().getName().equals(EventRequestStats.PENDING.name()))
                .collect(Collectors.toList());
    }

    private List<EventRequest> findEventRequestsByEvent(Event event) {
        return eventRequestRepo.findAllByEvent(event);
    }

    private List<EventRequest> findEventRequestsByIds(List<Long> requestIds) {
        return eventRequestRepo.findAllByIdIn(requestIds);
    }

    private void checkInitiator(User user, Event event) {
        if (!Objects.equals(event.getInitiator().getId(), user.getId())) {
            throw new OperationFailedException(
                    "Только создатель имеет право редактировать событие и получать запросы на участие"
            );
        }
    }

    private void checkUpdateAvailable(Event event) {
        if (event.getState().getName().equals(EventStates.PUBLISHED.name())) {
            throw new OperationFailedException(
                    "изменить можно только отмененные события или события в состоянии ожидания модерации "
            );
        }
    }

    private void checkProcessRequestsAvailable(Event event, List<EventRequest> confirmedRequests) {
        if (event.getParticipantLimit() - confirmedRequests.size() < 1) {
            throw new OperationFailedException(
                    "нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие "
            );
        }
    }
}
