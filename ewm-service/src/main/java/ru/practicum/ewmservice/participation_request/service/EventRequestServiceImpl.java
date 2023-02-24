package ru.practicum.ewmservice.participation_request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.storage.EventRepo;
import ru.practicum.ewmservice.participation_request.dto.EventRequestDto;
import ru.practicum.ewmservice.participation_request.dto.EventRequestStats;
import ru.practicum.ewmservice.participation_request.model.EventRequest;
import ru.practicum.ewmservice.participation_request.model.EventRequestStat;
import ru.practicum.ewmservice.participation_request.storage.EventRequestRepo;
import ru.practicum.ewmservice.participation_request.storage.EventRequestStatsRepo;
import ru.practicum.ewmservice.users.model.User;
import ru.practicum.ewmservice.users.storage.UserRepo;
import ru.practicum.ewmservice.util.exceptions.EntityNotExistException;
import ru.practicum.ewmservice.util.exceptions.OperationFaildException;
import ru.practicum.ewmservice.util.mappers.EventRequestMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Transactional(readOnly = true)
public class EventRequestServiceImpl implements EventRequestService {
    private final EventRequestRepo eventRequestRepo;
    private final UserRepo userRepo;
    private final EventRepo eventRepo;
    private final EventRequestStatsRepo eventRequestStatsRepo;

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
            throw new OperationFaildException(
                    "инициатор события не может добавить запрос на участие в своём событии"
            );
        }
        if (event.getState().getId() != 2) {
            throw new OperationFaildException(
                    "нельзя участвовать в неопубликованном событии"
            );
        }
        if (event.getConfirmedRequests() == event.getParticipantLimit()) {
            throw new OperationFaildException(
                    "достигнут лимит запросов на участие"
            );
        }
        if (eventRequestRepo.checkRequest(user.getId(), event.getId()) != 0) {
            throw new OperationFaildException(
                    "Невозможно добавить повторный запрос"
            );
        }
    }

    private void checkCancelAvailability(User user, EventRequest request) {
        if (request.getRequester().getId() != user.getId()) {
            throw new OperationFaildException(
                    "только инициатор запроса может отменить свой запрос"
            );
        }
    }

    private EventRequestStat findStatOrThrow(EventRequestStats stat) {
        return eventRequestStatsRepo.findByName(stat.name())
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Статус {} не существует", stat))
                );
    }

    private User findUserOrThrow(long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Пользователь c id = %s не существует", userId))
                );
    }

    private Event findEventOrThrow(long eventId) {
        return eventRepo.findById(eventId)
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Событие с id = %s не существует", eventId))
                );
    }

    private EventRequest findEventRequestOrThrow(long requestId) {
        return eventRequestRepo.findById(requestId)
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Запрос на участие с id = %s не существует", requestId))
                );
    }
}
