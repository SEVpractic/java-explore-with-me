package ru.practicum.ewmservice.event.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.categories.model.Category;
import ru.practicum.ewmservice.categories.storage.CategoryRepo;
import ru.practicum.ewmservice.compilation.storage.CompilationRepo;
import ru.practicum.ewmservice.event.dto.*;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.model.EventStates;
import ru.practicum.ewmservice.event.model.Location;
import ru.practicum.ewmservice.event.storage.EventRepo;
import ru.practicum.ewmservice.event.storage.EventStateRepo;
import ru.practicum.ewmservice.event.storage.LocationRepo;
import ru.practicum.ewmservice.participation_request.dto.EventRequestDto;
import ru.practicum.ewmservice.participation_request.dto.EventRequestStats;
import ru.practicum.ewmservice.participation_request.model.EventRequest;
import ru.practicum.ewmservice.participation_request.model.EventRequestStat;
import ru.practicum.ewmservice.participation_request.storage.EventRequestRepo;
import ru.practicum.ewmservice.participation_request.storage.EventRequestStatsRepo;
import ru.practicum.ewmservice.user.model.User;
import ru.practicum.ewmservice.user.storage.UserRepo;
import ru.practicum.ewmservice.util.exceptions.OperationFaildException;
import ru.practicum.ewmservice.util.mappers.EventMapper;
import ru.practicum.ewmservice.util.mappers.EventRequestMapper;
import ru.practicum.ewmservice.util.mappers.LocationMapper;
import ru.practicum.ewmservice.util.mappers.ProcessRequestResulMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class EventPrivateServiceImpl extends EventSuperService implements EventPrivateService {

    public EventPrivateServiceImpl(UserRepo userRepo,
                                   EventRepo eventRepo,
                                   CategoryRepo categoryRepo,
                                   LocationRepo locationRepo,
                                   EventStateRepo eventStateRepo,
                                   CompilationRepo compilationRepo,
                                   EventRequestRepo eventRequestRepo,
                                   EventRequestStatsRepo eventRequestStatsRepo) {
        super(userRepo, eventRepo, categoryRepo, locationRepo, eventStateRepo,
                compilationRepo, eventRequestRepo, eventRequestStatsRepo);
    }

    @Override
    @Transactional
    public EventFullDto create(EventIncomeDto dto, long userId) {
        User initiator = findUserOrThrow(userId);
        Category category = findCategoryOrThrow(dto.getCategoryId());
        Location location = findLocationOrSave(LocationMapper.toLocation(dto));
        Event event = EventMapper.toEvent(dto, category, location, initiator);

        event.setState(findEventStateOrThrow(EventStates.PENDING));
        event.setCreatedOn(LocalDateTime.now());
        event = eventRepo.save(event);
        log.info("Создано событие c id = {} ", event.getId());

        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto privateUpdate(EventIncomeDto dto, long userId, long eventId) {
        findUserOrThrow(userId);
        Event event = findEventOrThrow(eventId);

        if (event.getInitiator().getId() != userId) {
            throw new OperationFaildException(
                    "Только создатель и администратор имею право редактировать событие"
            );
        }
        event = update(event, dto);
        log.info("Обновлено событие c id = {} юзером с id = {}", eventId, userId);

        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getAll(long userId) {
        User initiator = findUserOrThrow(userId);

        List<Event> events = eventRepo.findAllByInitiator(initiator);
        log.info("Сформирован список ивентов пользователя c id = {} ", initiator);

        return EventMapper.toEventShortDto(events);
    }

    @Override
    public EventFullDto getById(long userId, long eventId) {
        findUserOrThrow(userId);

        Event event = findEventOrThrow(eventId);
        log.info("Возвращаю событие c id = {} ", eventId);

        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventRequestDto> getRequests(long userId, long eventId) {
        List<EventRequest> requests;
        User user = findUserOrThrow(userId);
        Event event = findEventOrThrow(eventId);
        checkInitiator(user, event);

        requests = findEventRequestsByEvent(event);
        log.info("Возвращаю перечень запросов на участие в событии c id = {} ", eventId);

        return EventRequestMapper.toEventRequestDto(requests);
    }

    @Override
    @Transactional
    public ProcessRequestResultDto processRequests(long userId, long eventId, ProcessRequestsDto dto) {
        User user = findUserOrThrow(userId);
        Event event = findEventOrThrow(eventId);
        checkInitiator(user, event);

        List<EventRequest> requests = findEventRequestsByIds(dto.getRequestIds());
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
        //todo если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
        //todo нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
        //todo статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
        //todo если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить
    }

    private void filterAndProcessRequests(List<EventRequest> requests, Event event) {
        if (event.getParticipantLimit() == 0) {
            confirmRequests(requests, event);
        } else {
            int limit = event.getParticipantLimit() - event.getConfirmedRequests();

            if (requests.size() < limit) {
                confirmRequests(requests, event);
            } else {
                confirmRequests(requests.subList(0, limit - 1), event);
                rejectRequests(requests.subList(limit, requests.size()));
            }
        }
    }

    private void confirmRequests(List<EventRequest> requests, Event event) {
        EventRequestStat stat = findRequestStatOrThrow(EventRequestStats.CONFIRMED);
        requests.forEach(request -> request.setStatus(stat));
        event.setConfirmedRequests(event.getConfirmedRequests() + requests.size());
    }

    private void rejectRequests(List<EventRequest> requests) {
        EventRequestStat stat = findRequestStatOrThrow(EventRequestStats.REJECTED);
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
        if (event.getInitiator().getId() != user.getId()) {
            throw new OperationFaildException(
                    "только инициатор события может работать с запросами"
            );
        }
    }
}
