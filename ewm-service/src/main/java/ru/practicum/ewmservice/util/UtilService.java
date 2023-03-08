package ru.practicum.ewmservice.util;

import com.querydsl.core.types.Predicate;
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
import ru.practicum.ewmservice.compilation.model.Compilation;
import ru.practicum.ewmservice.compilation.storage.CompilationRepo;
import ru.practicum.ewmservice.event.model.AdminComment;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.model.EventState;
import ru.practicum.ewmservice.event.model.EventStates;
import ru.practicum.ewmservice.event.storage.AdminCommentRepo;
import ru.practicum.ewmservice.event.storage.EventRepo;
import ru.practicum.ewmservice.event.storage.EventStateRepo;
import ru.practicum.ewmservice.participation_request.dto.EventRequestStats;
import ru.practicum.ewmservice.participation_request.model.EventRequest;
import ru.practicum.ewmservice.participation_request.model.EventRequestStat;
import ru.practicum.ewmservice.participation_request.storage.EventRequestRepo;
import ru.practicum.ewmservice.participation_request.storage.EventRequestStatsRepo;
import ru.practicum.ewmservice.user.model.User;
import ru.practicum.ewmservice.user.storage.UserRepo;
import ru.practicum.ewmservice.util.exceptions.EntityNotExistException;
import ru.practicum.ewmservice.util.mappers.ViewsMapper;
import ru.practicum.ewmservice.util.storage.EventQFilter;
import ru.practicum.ewmservice.util.storage.QPredicates;
import ru.practicum.statsclient.StatsClient;

import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewmservice.event.model.QEvent.event;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Transactional(readOnly = true)
public class UtilService {
    private final UserRepo userRepo;
    private final EventRepo eventRepo;
    private final StatsClient statsClient;
    private final CategoryRepo categoryRepo;
    private final EventRequestRepo requestRepo;
    private final EventStateRepo eventStateRepo;
    private final CompilationRepo compilationRepo;
    private final AdminCommentRepo adminCommentRepo;
    private final EventRequestRepo eventRequestRepo;
    private final EventRequestStatsRepo eventRequestStatsRepo;

    public EventRequestStat findRequestStatOrThrow(EventRequestStats stat) {
        return eventRequestStatsRepo.findByName(stat.name())
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Статус %s не существует", stat))
                );
    }

    public Event findPublicEventOrThrow(long eventId) {
        return eventRepo.findPublicById(eventId)
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Событие с id = %s не существует", eventId))
                );
    }

    public Event findEventOrThrow(long eventId) {
        return eventRepo.findById(eventId)
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Событие с id = %s не существует", eventId))
                );
    }

    public User findUserOrThrow(long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Пользователь c id = %s не существует", userId))
                );
    }

    public Category findCategoryOrThrow(long categoryId) {
        return categoryRepo.findById(categoryId)
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Категория c id = %s не существует", categoryId))
                );
    }

    public EventState findEventStateOrThrow(EventStates state) {
        return eventStateRepo.findByName(state.toString())
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Статус c name = %s не существует", state))
                );
    }

    public EventRequestStat findStatOrThrow(EventRequestStats stat) {
        return eventRequestStatsRepo.findByName(stat.name())
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Статус %s не существует", stat.name()))
                );
    }

    public EventRequest findEventRequestOrThrow(long requestId) {
        return eventRequestRepo.findById(requestId)
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Запрос на участие с id = %s не существует", requestId))
                );
    }

    public Compilation findCompilationOrThrow(long compId) {
        return compilationRepo.findById(compId)
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Подборка с id = %s не существует", compId))
                );
    }

    public Pageable createPageable(String sort, int from, int size) {
        return PageRequest.of(
                from == 0 ? 0 : (from / size),
                size,
                Sort.by(Sort.Direction.ASC, sort)
        );
    }

    public List<EventRequest> findConfirmedRequests(Event event) {
        return requestRepo.findConfirmedRequests(event.getId());
    }

    public Map<Event, List<EventRequest>> findConfirmedRequests(List<Event> events) {
        List<EventRequest> confirmedRequests = requestRepo.findConfirmedRequests(
                events.stream().map(Event::getId).collect(Collectors.toList())
        );

        return confirmedRequests.stream()
                .collect(Collectors.groupingBy(EventRequest::getEvent, Collectors.toList()));
    }

    public Map<Event, List<EventRequest>> findConfirmedRequestsByCompilations(List<Compilation> compilations) {
        Set<Event> events = new HashSet<>();
        compilations.forEach(c -> events.addAll(c.getEvents()));

        return findConfirmedRequests(new ArrayList<>(events));
    }

    public Map<Long, Integer> findViews(List<Event> events) {
        return ViewsMapper.toStatsMap(
                statsClient.getStat(events.stream().map(Event::getId).collect(Collectors.toList()))
        );
    }

    public Map<Long, Integer> findViews(Set<Event> events) {
        return ViewsMapper.toStatsMap(
                statsClient.getStat(events.stream().map(Event::getId).collect(Collectors.toList()))
        );
    }

    public Map<Long, Integer> findViews(Long eventId) {
        return ViewsMapper.toStatsMap(
                statsClient.getStat(List.of(eventId))
        );
    }

    public Map<Long, Integer> findViews(Compilation compilation) {
        return ViewsMapper.toStatsMap(
                statsClient.getStat(compilation.getEvents().stream().map(Event::getId).collect(Collectors.toList()))
        );
    }

    public List<Event> findByFilter(Pageable pageable, EventQFilter filter) {
        Predicate main = fillPredicate(filter);
        Predicate text = fillTextPredicate(filter);
        Predicate available = fillIsAvailablePredicate(filter);

        Predicate predicate = QPredicates.builder()
                .add(main)
                .add(text)
                .add(available)
                .buildAnd();


        return eventRepo.findAll(predicate, pageable).toList();
    }

    private Predicate fillPredicate(EventQFilter filter) {
        return QPredicates.builder()
                .add(filter.getEventIds(), event.id::in)
                .add(filter.getUserIds(), event.initiator.id::in)
                .add(filter.getStates(), event.state.name::in)
                .add(filter.getCategories(), event.category.id::in)
                .add(filter.getPaid(), event.paid::eq)
                .add(filter.getRangeStart(), event.eventDate::after)
                .add(filter.getRangeEnd(), event.eventDate::before)
                .buildAnd();
    }

    private Predicate fillTextPredicate(EventQFilter filter) {
        if (filter.getText() == null || filter.getText().isBlank()) return null;
        return QPredicates.builder()
                .add(event.annotation.likeIgnoreCase(filter.getText()))
                .add(event.description.likeIgnoreCase(filter.getText()))
                .buildOr();
    }

    private Predicate fillIsAvailablePredicate(EventQFilter filter) {
        if (filter.isOnlyAvailable()) {
            return event.state.id.in(2);
        } else return null;
    }

    public Map<Long, AdminComment> findByEventId(List<Event> events) {
        List<AdminComment> comments = adminCommentRepo.findLastByEventIds(
                events.stream()
                        .filter(e -> e.getState().getName().equals(EventStates.CANCELED.name()))
                        .collect(Collectors.toList())
        );

        return comments.stream()
                .collect(
                        Collectors.toMap(comment -> comment.getEvent().getId(), comment -> comment)
                );
    }
}
