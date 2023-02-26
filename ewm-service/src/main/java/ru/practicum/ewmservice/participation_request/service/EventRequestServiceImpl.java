package ru.practicum.ewmservice.participation_request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.categories.storage.CategoryRepo;
import ru.practicum.ewmservice.compilation.storage.CompilationRepo;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.storage.EventRepo;
import ru.practicum.ewmservice.event.storage.EventStateRepo;
import ru.practicum.ewmservice.event.storage.LocationRepo;
import ru.practicum.ewmservice.participation_request.dto.EventRequestDto;
import ru.practicum.ewmservice.participation_request.dto.EventRequestStats;
import ru.practicum.ewmservice.participation_request.model.EventRequest;
import ru.practicum.ewmservice.participation_request.storage.EventRequestRepo;
import ru.practicum.ewmservice.participation_request.storage.EventRequestStatsRepo;
import ru.practicum.ewmservice.user.model.User;
import ru.practicum.ewmservice.user.storage.UserRepo;
import ru.practicum.ewmservice.util.exceptions.OperationFailedException;
import ru.practicum.ewmservice.util.mappers.EventRequestMapper;
import ru.practicum.ewmservice.util.UtilService;
import ru.practicum.statsclient.StatsClientImpl;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
public class EventRequestServiceImpl extends UtilService implements EventRequestService {
    public EventRequestServiceImpl(UserRepo userRepo,
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
    @Transactional
    public EventRequestDto create(long userId, long eventId) {
        EventRequest eventRequest;

        User user = findUserOrThrow(userId);
        Event event = findEventOrThrow(eventId);
        checkCreateAvailability(user, event);

        eventRequest = save(user, event);
        log.info("Создан запрос c id = {} ", eventRequest.getId());

        return EventRequestMapper.toEventRequestDto(eventRequest);
    }

    @Override
    public List<EventRequestDto> getAll(long userId) {
        List<EventRequest> eventRequests;

        User user = findUserOrThrow(userId);
        eventRequests = eventRequestRepo.findAllByRequester(user);
        log.info("Создан список запросов пользователя c id = {} ", userId);

        return EventRequestMapper.toEventRequestDto(eventRequests);
    }

    @Override
    @Transactional
    public EventRequestDto cansel(long userId, long requestId) {
        User user = findUserOrThrow(userId);
        EventRequest request = findEventRequestOrThrow(requestId);

        checkCancelAvailability(user, request);
        request.setStatus(findStatOrThrow(EventRequestStats.CANCELED));
        log.info("Отменен запрос c id = {} ", requestId);

        return EventRequestMapper.toEventRequestDto(request);
    }

    private EventRequest save(User user, Event event) {
        EventRequest eventRequest = new EventRequest();

        eventRequest.setCreated(LocalDateTime.now());
        eventRequest.setEvent(event);
        eventRequest.setRequester(user);

        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            eventRequest.setStatus(findStatOrThrow(EventRequestStats.PENDING));
        } else {
            eventRequest.setStatus(findStatOrThrow(EventRequestStats.CONFIRMED));
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }

        return eventRequestRepo.save(eventRequest);
    }

    private void checkCreateAvailability(User user, Event event) {
        if (event.getInitiator().getId() == user.getId()) {
            throw new OperationFailedException(
                    "инициатор события не может добавить запрос на участие в своём событии"
            );
        }
        if (event.getState().getId() != 2) {
            throw new OperationFailedException(
                    "нельзя участвовать в неопубликованном событии"
            );
        }
        if (event.getConfirmedRequests() == event.getParticipantLimit()) {
            throw new OperationFailedException(
                    "достигнут лимит запросов на участие"
            );
        }
        if (eventRequestRepo.checkRequest(user.getId(), event.getId()) != 0) {
            throw new OperationFailedException(
                    "Невозможно добавить повторный запрос"
            );
        }
    }

    private void checkCancelAvailability(User user, EventRequest request) {
        if (request.getRequester().getId() != user.getId()) {
            throw new OperationFailedException(
                    "только инициатор запроса может отменить свой запрос"
            );
        }
    }
}
