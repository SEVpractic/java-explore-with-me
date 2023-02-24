package ru.practicum.ewmservice.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.categories.model.Category;
import ru.practicum.ewmservice.categories.storage.CategoryRepo;
import ru.practicum.ewmservice.event.dto.*;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.model.EventState;
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
import ru.practicum.ewmservice.users.model.User;
import ru.practicum.ewmservice.users.storage.UserRepo;
import ru.practicum.ewmservice.util.exceptions.EntityNotExistException;
import ru.practicum.ewmservice.util.exceptions.OperationFaildException;
import ru.practicum.ewmservice.util.mappers.EventMapper;
import ru.practicum.ewmservice.util.mappers.EventRequestMapper;
import ru.practicum.ewmservice.util.mappers.LocationMapper;
import ru.practicum.ewmservice.util.mappers.ProcessRequestResulMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final UserRepo userRepo;
    private final EventRepo eventRepo;
    private final CategoryRepo categoryRepo;
    private final LocationRepo locationRepo;
    private final EventStateRepo eventStateRepo;
    private final EventRequestRepo eventRequestRepo;
    private final EventRequestStatsRepo eventRequestStatsRepo;

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
    @Transactional
    public EventFullDto adminUpdate(EventIncomeDto dto, long eventId) {
        Event event = findEventOrThrow(eventId);

        event = update(event, dto);
        log.info("Обновлено событие c id = {} администратором", eventId);

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
    public List<EventShortDto> getAllPublic(String text, List<Long> categories, boolean paid,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            boolean onlyAvailable,
                                            EventSorts sort,
                                            int from, int size) {
        List<Event> events;

        Pageable pageable = createPageable(sort, from, size);
        events = findByText(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable);
        //todo информация о каждом событии должна включать в себя количество просмотров и количество уже одобренных заявок на участие
        //todo информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики

        return EventMapper.toEventShortDto(events);
    }

    @Override
    public List<EventFullDto> getAllAdmin(List<Long> userIds,
                                          List<EventStates> states,
                                          List<Long> categories,
                                          LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd,
                                          int from, int size) {
        List<Event> events;

        Pageable pageable = createPageable(EventSorts.EVENT_DATE, from, size);
        events = findByUsersAndStates(userIds, states.stream().map(Enum::name).collect(Collectors.toList()),
                categories, rangeStart, rangeEnd, pageable);

        return EventMapper.toEventFullDto(events);
    }

    @Override
    public EventFullDto getById(long userId, long eventId) {
        findUserOrThrow(userId);

        Event event = findEventOrThrow(eventId);
        log.info("Возвращаю событие c id = {} ", eventId);

        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto getPublicById(long eventId) {
        Event event = findPublicEventOrThrow(eventId);
        log.info("Возвращаю событие c id = {} ", eventId);
        //todo информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов
        //todo информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
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

    private EventRequestStat findRequestStatOrThrow(EventRequestStats stat) {
        return eventRequestStatsRepo.findByName(stat.name())
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Статус %s не существует", stat))
                );
    }

    private Event findPublicEventOrThrow(long eventId) {
        return eventRepo.findPublicById(eventId)
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Событие с id = %s не существует", eventId))
                );
    }

    private Event findEventOrThrow(long eventId) {
        return eventRepo.findById(eventId)
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Событие с id = %s не существует", eventId))
                );
    }

    private User findUserOrThrow(long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Пользователь c id = %s не существует", userId))
                );
    }

    private Category findCategoryOrThrow(long categoryId) {
        return categoryRepo.findById(categoryId)
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Категория c id = %s не существует", categoryId))
                );
    }

    private EventState findEventStateOrThrow(EventStates state) {
        return eventStateRepo.findByName(state.toString())
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Статус c name = %s не существует", state))
                );
    }

    private Location findLocationOrSave(Location location) {
        return locationRepo.find(location.getLat(), location.getLon())
                .orElseGet(() -> locationRepo.save(location));
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

    private List<Event> findEventsByDateRange(String text, List<Long> categories, boolean paid,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                              boolean onlyAvailable, Pageable pageable) {
        if (onlyAvailable) {
            return eventRepo.findAvailableEventsByDateRange(text, categories, paid, rangeStart, rangeEnd, pageable);
        } else {
            return eventRepo.findEventsByDateRange(text, categories, paid, rangeStart, rangeEnd, pageable);
        }
    }

    private List<Event> findEventsByUsersAndDateRange(List<Long> userIds, List<String> states, List<Long> categories,
                                                      LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                      Pageable pageable) {
        return eventRepo.findEventsByUsersAndDateRange(userIds, states, categories, rangeStart, rangeEnd, pageable);
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

    private List<Event> findEventsByUsersAndStartDate(List<Long> userIds, List<String> states,
                                                      List<Long> categories, LocalDateTime rangeStart,
                                                      Pageable pageable) {
        return eventRepo.findEventsByUsersStartDate(userIds, states, categories, rangeStart, pageable);
    }

    private List<Event> findEventsFromNow(String text, List<Long> categories, boolean paid,
                                          boolean onlyAvailable, Pageable pageable) {
        if (onlyAvailable) {
            return eventRepo.findAvailableEventsFromNow(text, categories, paid, pageable);
        } else {
            return eventRepo.findEventsFromNow(text, categories, paid, pageable);
        }
    }

    private List<Event> findEventsByUsersFromNow(List<Long> userIds, List<String> states,
                                                 List<Long> categories, Pageable pageable) {
        return eventRepo.findEventsByUsers(userIds, states, categories, pageable);
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

    private Pageable createPageable(EventSorts sort, int from, int size) {
        if (sort.equals(EventSorts.VIEWS)) {
            return PageRequest.of(
                    from == 0 ? 0 : (from / size),
                    size,
                    Sort.by(Sort.Direction.ASC, "views")
            );
        } else {
            return PageRequest.of(
                    from == 0 ? 0 : (from / size),
                    size,
                    Sort.by(Sort.Direction.ASC, "eventDate")
            );
        }
    }

    private Event update(Event event, EventIncomeDto dto) {
        if (dto.getAnnotation() != null) event.setAnnotation(dto.getAnnotation());
        if (dto.getCategoryId() != null) event.setCategory(findCategoryOrThrow(dto.getCategoryId()));
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getEventDate() != null) event.setEventDate(dto.getEventDate());
        if (dto.getLocation() != null) updateLocation(event, dto);
        if (dto.getPaid() != null) event.setPaid(dto.getPaid());
        if (dto.getParticipantLimit() != null) event.setParticipantLimit(dto.getParticipantLimit());
        if (dto.getRequestModeration() != null) event.setRequestModeration(dto.getRequestModeration());
        if (dto.getStateAction() != null) updateState(event, dto);
        if (dto.getTitle() != null) event.setTitle(dto.getTitle());

        return event;
    }

    private void updateState(Event event, EventIncomeDto dto) {
        StateActions action = dto.getStateAction();
        switch (action) {
            case CANCEL_REVIEW:
                event.setState(findEventStateOrThrow(EventStates.CANCELED));
                break;
            case PUBLISH_EVENT:
                if (event.getEventDate().isAfter(LocalDateTime.now().plusHours(1))
                        && event.getState().getId() == 1) {
                    event.setState(findEventStateOrThrow(EventStates.PUBLISHED));
                    event.setPublishedOn(LocalDateTime.now());
                } else if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                    throw new OperationFaildException(
                            "Невозможно опубликовать мероприятие, если время начала меньше чем через час"
                    );
                } else if (event.getState().getId() != 1) {
                    throw new OperationFaildException(
                            "Невозможно опубликовать мероприятие, не ожидающее публикацию"
                    );
                }
                break;
            case REJECT_EVENT:
                if (!event.getState().getName().equals(EventStates.PUBLISHED.name())) {
                    event.setState(findEventStateOrThrow(EventStates.CANCELED));
                } else {
                    throw new OperationFaildException(
                            "Невозможно отклонить опубликованное мероприятие"
                    );
                }
                break;
        }
    }

    private void updateLocation(Event event, EventIncomeDto dto) {
        Location location = findLocationOrSave(LocationMapper.toLocation(dto));
        event.setLocation(location);
    }
}
