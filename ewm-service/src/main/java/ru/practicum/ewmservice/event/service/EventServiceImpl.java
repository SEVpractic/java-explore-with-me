package ru.practicum.ewmservice.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.categories.model.Category;
import ru.practicum.ewmservice.categories.storage.CategoryRepo;
import ru.practicum.ewmservice.event.dto.EventFullDto;
import ru.practicum.ewmservice.event.dto.EventIncomeDto;
import ru.practicum.ewmservice.event.dto.EventShortDto;
import ru.practicum.ewmservice.event.dto.StateActions;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.model.EventState;
import ru.practicum.ewmservice.event.model.EventStates;
import ru.practicum.ewmservice.event.model.Location;
import ru.practicum.ewmservice.event.storage.EventRepo;
import ru.practicum.ewmservice.event.storage.EventStateRepo;
import ru.practicum.ewmservice.event.storage.LocationRepo;
import ru.practicum.ewmservice.users.model.User;
import ru.practicum.ewmservice.users.storage.UserRepo;
import ru.practicum.ewmservice.util.exceptions.EntityNotExistException;
import ru.practicum.ewmservice.util.mappers.EventMapper;
import ru.practicum.ewmservice.util.mappers.LocationMapper;

import java.time.LocalDateTime;
import java.util.List;

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

    @Override
    @Transactional
    public EventFullDto create(EventIncomeDto dto, long userId) {
        User initiator = findUserOrThrow(userId);
        Category category = findCategoryOrThrow(dto.getCategoryId());
        Location location = findOrSave(LocationMapper.toLocation(dto));
        Event event = EventMapper.toEvent(dto, category, location, initiator);

        event.setState(findEventStateOrThrow(EventStates.PENDING));
        event.setCreatedOn(LocalDateTime.now());
        event = eventRepo.save(event);
        log.info("Создано событие c id = {} ", event.getId());

        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto update(EventIncomeDto dto, long userId, long eventId) {
        findUserOrThrow(userId);
        Event event = findEventOrThrow(eventId);

        if (dto.getAnnotation() != null) event.setAnnotation(dto.getAnnotation());
        if (dto.getCategoryId() != null) event.setCategory(findCategoryOrThrow(dto.getCategoryId()));
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getEventDate() != null) event.setEventDate(dto.getEventDate());
        if (dto.getLocation() != null) {
            Location location = findOrSave(LocationMapper.toLocation(dto));
            event.setLocation(location);
        }
        if (dto.getPaid() != null) event.setPaid(dto.getPaid());
        if (dto.getParticipantLimit() != null) event.setParticipantLimit(dto.getParticipantLimit());
        if (dto.getRequestModeration() != null) event.setRequestModeration(dto.getRequestModeration());
        if (dto.getStateAction() != null) {
            StateActions action = dto.getStateAction();
            if (action.equals(StateActions.CANCEL_REVIEW)) event.setState(findEventStateOrThrow(EventStates.CANCELED));
        }
        if (dto.getTitle() != null) event.setTitle(dto.getTitle());

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

    private Location findOrSave(Location location) {
        return locationRepo.find(location.getLat(), location.getLon())
                .orElseGet(() -> locationRepo.save(location));
    }
}
