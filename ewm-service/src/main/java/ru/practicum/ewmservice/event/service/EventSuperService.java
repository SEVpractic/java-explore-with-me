package ru.practicum.ewmservice.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.event.dto.EventIncomeDto;
import ru.practicum.ewmservice.event.dto.EventSorts;
import ru.practicum.ewmservice.event.dto.StateActions;
import ru.practicum.ewmservice.event.model.AdminComment;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.model.EventStates;
import ru.practicum.ewmservice.event.model.Location;
import ru.practicum.ewmservice.event.storage.AdminCommentRepo;
import ru.practicum.ewmservice.event.storage.LocationRepo;
import ru.practicum.ewmservice.util.UtilService;
import ru.practicum.ewmservice.util.exceptions.EventDateValidationException;
import ru.practicum.ewmservice.util.exceptions.OperationFailedException;
import ru.practicum.ewmservice.util.mappers.AdminCommentMapper;
import ru.practicum.ewmservice.util.mappers.LocationMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventSuperService {
    private final UtilService utilService;
    private final LocationRepo locationRepo;
    private final AdminCommentRepo commentRepo;

    public Event update(Event event, EventIncomeDto dto) {
        if (dto.getAnnotation() != null && !dto.getAnnotation().isBlank()) event.setAnnotation(dto.getAnnotation());
        if (dto.getCategoryId() != null) event.setCategory(utilService.findCategoryOrThrow(dto.getCategoryId()));
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) event.setDescription(dto.getDescription());
        if (dto.getEventDate() != null) event.setEventDate(dto.getEventDate());
        if (dto.getLocation() != null) updateLocation(event, dto);
        if (dto.getPaid() != null) event.setPaid(dto.getPaid());
        if (dto.getParticipantLimit() != null) event.setParticipantLimit(dto.getParticipantLimit());
        if (dto.getRequestModeration() != null) event.setRequestModeration(dto.getRequestModeration());
        if (dto.getStateAction() != null) updateState(event, dto.getStateAction());
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) event.setTitle(dto.getTitle());

        return event;
    }

    private void updateState(Event event, StateActions action) {
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
        event.setState(utilService.findEventStateOrThrow(EventStates.CANCELED));
    }

    private void updateToPublish(Event event) {
        if (event.getEventDate().isAfter(LocalDateTime.now().plusHours(1))
                && event.getState().getId() == 1) {
            event.setState(utilService.findEventStateOrThrow(EventStates.PUBLISHED));
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
            event.setState(utilService.findEventStateOrThrow(EventStates.CANCELED));
        } else {
            throw new OperationFailedException(
                    "Невозможно отклонить опубликованное мероприятие"
            );
        }
    }

    public void updateToPending(Event event) {
        event.setState(utilService.findEventStateOrThrow(EventStates.PENDING));
    }

    private void updateLocation(Event event, EventIncomeDto dto) {
        Location location = findLocationOrSave(LocationMapper.toLocation(dto));
        event.setLocation(location);
    }

    public Pageable createPageableBySort(EventSorts sort, int from, int size) {
        if (sort.equals(EventSorts.VIEWS)) {
            return utilService.createPageable("views", from, size);
        } else {
            return utilService.createPageable("eventDate", from, size);
        }
    }

    public Location findLocationOrSave(Location location) {
        return locationRepo.find(location.getLat(), location.getLon())
                .orElseGet(() -> locationRepo.save(location));
    }

    public void checkEventDate(EventIncomeDto dto, int hours) {
        if (dto.getEventDate() != null
                && dto.getEventDate().isBefore(LocalDateTime.now().plusHours(hours))) {
            throw new EventDateValidationException(
                    String.format("Время между началом события и текущем моментом не может быть меньше %s часов", hours)
            );
        }
    }

    public void saveAdminComment(Long eventId, EventIncomeDto dto) {
        if (dto.getComment() != null && !dto.getComment().isBlank()
                && dto.getStateAction().name().equals(StateActions.REJECT_EVENT.name())) {
            AdminComment comment = AdminCommentMapper.toAdminComment(eventId, dto);
            comment.setCreatedOn(LocalDateTime.now());

            commentRepo.save(comment);
        }
    }

    public void saveAdminComment(List<EventIncomeDto> dto) {
        List<AdminComment> comments = AdminCommentMapper.toAdminComment(dto);
        comments.forEach(comment -> comment.setCreatedOn(LocalDateTime.now()));

        commentRepo.saveAll(comments);
    }
}
