package ru.practicum.ewmservice.event.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.categories.storage.CategoryRepo;
import ru.practicum.ewmservice.compilation.storage.CompilationRepo;
import ru.practicum.ewmservice.event.dto.EventIncomeDto;
import ru.practicum.ewmservice.event.dto.EventSorts;
import ru.practicum.ewmservice.event.dto.StateActions;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.model.EventStates;
import ru.practicum.ewmservice.event.model.Location;
import ru.practicum.ewmservice.event.storage.EventRepo;
import ru.practicum.ewmservice.event.storage.EventStateRepo;
import ru.practicum.ewmservice.event.storage.LocationRepo;
import ru.practicum.ewmservice.participation_request.storage.EventRequestRepo;
import ru.practicum.ewmservice.participation_request.storage.EventRequestStatsRepo;
import ru.practicum.ewmservice.user.storage.UserRepo;
import ru.practicum.ewmservice.util.UtilService;
import ru.practicum.ewmservice.util.exceptions.OperationFailedException;
import ru.practicum.ewmservice.util.mappers.LocationMapper;

import java.time.LocalDateTime;

@Service
public class EventSuperService extends UtilService {
    public EventSuperService(UserRepo userRepo,
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

    protected Event update(Event event, EventIncomeDto dto) {
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
                updateToCancel(event);
                break;
            case PUBLISH_EVENT:
                updateToPublish(event);
                break;
            case REJECT_EVENT:
                updateToReject(event);
                break;
            case SEND_TO_REVIEW:
                updateToPending(event);
                break;
        }
    }

    private void updateToCancel(Event event) {
        event.setState(findEventStateOrThrow(EventStates.CANCELED));
    }

    private void updateToPublish(Event event) {
        if (event.getEventDate().isAfter(LocalDateTime.now().plusHours(1))
                && event.getState().getId() == 1) {
            event.setState(findEventStateOrThrow(EventStates.PUBLISHED));
            event.setPublishedOn(LocalDateTime.now());
        } else if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new OperationFailedException(
                    "Невозможно опубликовать мероприятие, если время начала меньше чем через час"
            );
        } else if (event.getState().getId() != 1) {
            throw new OperationFailedException(
                    "Невозможно опубликовать мероприятие, не ожидающее публикацию"
            );
        }
    }

    public void updateToReject(Event event) {
        if (!event.getState().getName().equals(EventStates.PUBLISHED.name())) {
            event.setState(findEventStateOrThrow(EventStates.CANCELED));
        } else {
            throw new OperationFailedException(
                    "Невозможно отклонить опубликованное мероприятие"
            );
        }
    }

    public void updateToPending(Event event) {
        event.setState(findEventStateOrThrow(EventStates.PENDING));
    }

    private void updateLocation(Event event, EventIncomeDto dto) {
        Location location = findLocationOrSave(LocationMapper.toLocation(dto));
        event.setLocation(location);
    }

    protected Pageable createPageableBySort(EventSorts sort, int from, int size) {
        if (sort.equals(EventSorts.VIEWS)) {
            return createPageable("views", from, size);
        } else {
            return createPageable("eventDate", from, size);
        }
    }

    protected Location findLocationOrSave(Location location) {
        return locationRepo.find(location.getLat(), location.getLon())
                .orElseGet(() -> locationRepo.save(location));
    }
}
