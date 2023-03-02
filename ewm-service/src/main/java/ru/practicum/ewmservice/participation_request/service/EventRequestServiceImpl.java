package ru.practicum.ewmservice.participation_request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.participation_request.dto.EventRequestDto;
import ru.practicum.ewmservice.participation_request.dto.EventRequestStats;
import ru.practicum.ewmservice.participation_request.model.EventRequest;
import ru.practicum.ewmservice.participation_request.storage.EventRequestRepo;
import ru.practicum.ewmservice.user.model.User;
import ru.practicum.ewmservice.util.UtilService;
import ru.practicum.ewmservice.util.exceptions.OperationFailedException;
import ru.practicum.ewmservice.util.mappers.EventRequestMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventRequestServiceImpl implements EventRequestService {
    private final EventRequestRepo eventRequestRepo;
    private final UtilService utilService;

    @Override
    @Transactional
    public EventRequestDto create(long userId, long eventId) {
        EventRequest eventRequest;

        User user = utilService.findUserOrThrow(userId);
        Event event = utilService.findEventOrThrow(eventId);
        List<EventRequest> confirmedRequests = utilService.findConfirmedRequests(event);
        checkCreateAvailability(user, event, confirmedRequests);

        eventRequest = save(user, event);
        log.info("Создан запрос c id = {} ", eventRequest.getId());

        return EventRequestMapper.toEventRequestDto(eventRequest);
    }

    @Override
    public List<EventRequestDto> getAll(long userId) {
        List<EventRequest> eventRequests;

        User user = utilService.findUserOrThrow(userId);
        eventRequests = eventRequestRepo.findAllByRequester(user);
        log.info("Создан список запросов пользователя c id = {} ", userId);

        return EventRequestMapper.toEventRequestDto(eventRequests);
    }

    @Override
    @Transactional
    public EventRequestDto cansel(long userId, long requestId) {
        User user = utilService.findUserOrThrow(userId);
        EventRequest request = utilService.findEventRequestOrThrow(requestId);

        checkCancelAvailability(user, request);
        request.setStatus(utilService.findStatOrThrow(EventRequestStats.CANCELED));
        log.info("Отменен запрос c id = {} ", requestId);

        return EventRequestMapper.toEventRequestDto(request);
    }

    private EventRequest save(User user, Event event) {
        EventRequest eventRequest = new EventRequest();

        eventRequest.setCreated(LocalDateTime.now());
        eventRequest.setEvent(event);
        eventRequest.setRequester(user);

        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            eventRequest.setStatus(utilService.findStatOrThrow(EventRequestStats.PENDING));
        } else {
            eventRequest.setStatus(utilService.findStatOrThrow(EventRequestStats.CONFIRMED));
        }

        return eventRequestRepo.save(eventRequest);
    }

    private void checkCreateAvailability(User user, Event event, List<EventRequest> confirmedRequests) {
        if (Objects.equals(event.getInitiator().getId(), user.getId())) {
            throw new OperationFailedException(
                    "инициатор события не может добавить запрос на участие в своём событии"
            );
        }
        if (event.getState().getId() != 2) {
            throw new OperationFailedException(
                    "нельзя участвовать в неопубликованном событии"
            );
        }
        if (confirmedRequests.size() == event.getParticipantLimit()) {
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
        if (!Objects.equals(request.getRequester().getId(), user.getId())) {
            throw new OperationFailedException(
                    "только инициатор запроса может отменить свой запрос"
            );
        }
    }
}
