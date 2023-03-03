package ru.practicum.ewmservice.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.compilation.dto.CompilationIncomeDto;
import ru.practicum.ewmservice.compilation.model.Compilation;
import ru.practicum.ewmservice.compilation.storage.CompilationRepo;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.storage.EventRepo;
import ru.practicum.ewmservice.participation_request.model.EventRequest;
import ru.practicum.ewmservice.util.UtilService;
import ru.practicum.ewmservice.util.mappers.CompilationsMapper;

import java.util.*;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CompilationServiceImpl implements CompilationService {
    private final EventRepo eventRepo;
    private final UtilService utilService;
    private final CompilationRepo compilationRepo;

    @Override
    @Transactional
    public CompilationDto create(CompilationIncomeDto dto) {
        Map<Long, Integer> views;
        Map<Event, List<EventRequest>> confirmedRequests;
        Compilation compilation = CompilationsMapper.toCompilation(dto);
        compilation.setEvents(findEvensByIds(dto.getEventIds()));

        compilation = compilationRepo.save(compilation);
        confirmedRequests = utilService.findConfirmedRequests(
                new ArrayList<>(compilation.getEvents())
        );
        views = getViews(compilation);
        log.info("Создана подборка c id = {} ", compilation.getId());

        return CompilationsMapper.toCompilationDto(compilation, confirmedRequests, views);
    }

    @Override
    @Transactional
    public CompilationDto update(CompilationIncomeDto dto, long compId) {
        Map<Long, Integer> views;
        Map<Event, List<EventRequest>> confirmedRequests;
        Compilation compilation = utilService.findCompilationOrThrow(compId);

        if (dto.getPinned() != null) compilation.setPinned(dto.getPinned());
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) compilation.setTitle(dto.getTitle());
        if (dto.getEventIds() != null && !dto.getEventIds().isEmpty()) {
            compilation.setEvents(findEvensByIds(dto.getEventIds()));
        }

        confirmedRequests = utilService.findConfirmedRequests(
                new ArrayList<>(compilation.getEvents())
        );
        views = getViews(compilation);
        log.info("Обновлена подборка c id = {} ", compilation.getId());

        return CompilationsMapper.toCompilationDto(compilation, confirmedRequests, views);
    }

    @Override
    @Transactional
    public CompilationDto delete(long compId) {
        Map<Long, Integer> views;
        Map<Event, List<EventRequest>> confirmedRequests;
        Compilation compilation = utilService.findCompilationOrThrow(compId);

        compilationRepo.delete(compilation);
        confirmedRequests = utilService.findConfirmedRequests(
                new ArrayList<>(compilation.getEvents())
        );
        views = getViews(compilation);
        log.info("Удалена подборка c id = {} ", compilation.getId());

        return CompilationsMapper.toCompilationDto(compilation, confirmedRequests, views);
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        Set<Event> events = new HashSet<>();
        Map<Long, Integer> views;
        Map<Event, List<EventRequest>> confirmedRequests;
        List<Compilation> compilations;
        Pageable pageable = utilService.createPageable("id", from, size);

        if (pinned != null) {
            compilations = findAllByPinned(pinned, pageable);
            log.info("Возвращаю коллекцию подборок событий по запросу");
        } else {
            compilations = findAll(pageable);
            log.info("Возвращаю коллекцию подборок событий по запросу");
        }

        compilations.forEach(compilation -> events.addAll(compilation.getEvents()));
        confirmedRequests = utilService.findConfirmedRequestsByCompilations(compilations);
        views = utilService.findViews(events);

        return CompilationsMapper.toCompilationDto(compilations, confirmedRequests, views);
    }

    @Override
    public CompilationDto getById(long compId) {
        Map<Long, Integer> views;
        Map<Event, List<EventRequest>> confirmedRequests;

        Compilation compilation = utilService.findCompilationOrThrow(compId);
        confirmedRequests = utilService.findConfirmedRequests(
                new ArrayList<>(compilation.getEvents())
        );
        views = getViews(compilation);
        log.info("Возвращаю подборку c id = {} ", compilation.getId());

        return CompilationsMapper.toCompilationDto(compilation, confirmedRequests, views);
    }

    private List<Compilation> findAllByPinned(boolean pinned, Pageable pageable) {
        return compilationRepo.findAllByPinned(pinned, pageable).toList();
    }

    private List<Compilation> findAll(Pageable pageable) {
        return compilationRepo.findAll(pageable).toList();
    }

    private Set<Event> findEvensByIds(List<Long> ids) {
        return eventRepo.findByIdIn(ids);
    }

    private Map<Long, Integer> getViews(Compilation compilation) {
        if (compilation.getEvents() != null && !compilation.getEvents().isEmpty()) {
            return utilService.findViews(compilation);
        } else {
            return Map.of();
        }
    }
}
