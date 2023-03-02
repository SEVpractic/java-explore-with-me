package ru.practicum.ewmservice.util;

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
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.model.EventState;
import ru.practicum.ewmservice.event.model.EventStates;
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
import ru.practicum.statsclient.StatsClient;
import ru.practicum.statsdto.Stat;

import java.util.*;
import java.util.stream.Collectors;

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

    public Map<Long, List<Stat>> findViews(List<Event> events) {
        return ViewsMapper.toStatsMap(
                statsClient.getStat(events.stream().map(Event::getId).collect(Collectors.toList()))
        );
    }

    public Map<Long, List<Stat>> findViews(Set<Event> events) {
        return ViewsMapper.toStatsMap(
                statsClient.getStat(events.stream().map(Event::getId).collect(Collectors.toList()))
        );
    }

    public Map<Long, List<Stat>> findViews(Long eventId) {
        return ViewsMapper.toStatsMap(
                statsClient.getStat(List.of(eventId))
        );
    }

    public Map<Long, List<Stat>> findViews(Compilation compilation) { // todo
        return ViewsMapper.toStatsMap(
                statsClient.getStat(compilation.getEvents().stream().map(Event::getId).collect(Collectors.toList()))
        );
    }
}
