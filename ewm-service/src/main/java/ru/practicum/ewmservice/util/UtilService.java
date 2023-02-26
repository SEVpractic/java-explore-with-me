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
import ru.practicum.ewmservice.event.storage.LocationRepo;
import ru.practicum.ewmservice.participation_request.dto.EventRequestStats;
import ru.practicum.ewmservice.participation_request.model.EventRequest;
import ru.practicum.ewmservice.participation_request.model.EventRequestStat;
import ru.practicum.ewmservice.participation_request.storage.EventRequestRepo;
import ru.practicum.ewmservice.participation_request.storage.EventRequestStatsRepo;
import ru.practicum.ewmservice.user.model.User;
import ru.practicum.ewmservice.user.storage.UserRepo;
import ru.practicum.ewmservice.util.exceptions.EntityNotExistException;
import ru.practicum.ewmservice.util.mappers.HitDtoMapper;
import ru.practicum.statsclient.StatsClientImpl;
import ru.practicum.statsdto.HitDto;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Transactional(readOnly = true)
public class UtilService {
    protected final UserRepo userRepo;
    protected final EventRepo eventRepo;
    protected final CategoryRepo categoryRepo;
    protected final LocationRepo locationRepo;
    protected final EventStateRepo eventStateRepo;
    protected final CompilationRepo compilationRepo;
    protected final EventRequestRepo eventRequestRepo;
    protected final EventRequestStatsRepo eventRequestStatsRepo;
    private final StatsClientImpl statsClient;

    protected EventRequestStat findRequestStatOrThrow(EventRequestStats stat) {
        return eventRequestStatsRepo.findByName(stat.name())
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Статус %s не существует", stat))
                );
    }

    protected Event findPublicEventOrThrow(long eventId) {
        return eventRepo.findPublicById(eventId)
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Событие с id = %s не существует", eventId))
                );
    }

    protected Event findEventOrThrow(long eventId) {
        return eventRepo.findById(eventId)
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Событие с id = %s не существует", eventId))
                );
    }

    protected User findUserOrThrow(long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Пользователь c id = %s не существует", userId))
                );
    }

    protected Category findCategoryOrThrow(long categoryId) {
        return categoryRepo.findById(categoryId)
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Категория c id = %s не существует", categoryId))
                );
    }

    protected EventState findEventStateOrThrow(EventStates state) {
        return eventStateRepo.findByName(state.toString())
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Статус c name = %s не существует", state))
                );
    }

    protected EventRequestStat findStatOrThrow(EventRequestStats stat) {
        return eventRequestStatsRepo.findByName(stat.name())
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Статус %s не существует", stat.name()))
                );
    }

    protected EventRequest findEventRequestOrThrow(long requestId) {
        return eventRequestRepo.findById(requestId)
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Запрос на участие с id = %s не существует", requestId))
                );
    }

    protected Compilation findCompilationOrThrow(long compId) {
        return compilationRepo.findById(compId)
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Подборка с id = %s не существует", compId))
                );
    }

    protected Pageable createPageable(String sort, int from, int size) {
        return PageRequest.of(
                from == 0 ? 0 : (from / size),
                size,
                Sort.by(Sort.Direction.ASC, sort)
        );
    }

    protected void saveStat(long eventId, String ip) {
        try {
            HitDto dto = HitDtoMapper.fillHit(eventId, ip);
            statsClient.saveHit(dto);
        } catch (RuntimeException ex) {
            log.info(ex.getMessage());
        }
    }
}
