package ru.practicum.ewmservice.compilation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.categories.storage.CategoryRepo;
import ru.practicum.ewmservice.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.compilation.dto.CompilationIncomeDto;
import ru.practicum.ewmservice.compilation.model.Compilation;
import ru.practicum.ewmservice.compilation.storage.CompilationRepo;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.storage.EventRepo;
import ru.practicum.ewmservice.event.storage.EventStateRepo;
import ru.practicum.ewmservice.event.storage.LocationRepo;
import ru.practicum.ewmservice.participation_request.storage.EventRequestRepo;
import ru.practicum.ewmservice.participation_request.storage.EventRequestStatsRepo;
import ru.practicum.ewmservice.user.storage.UserRepo;
import ru.practicum.ewmservice.util.UtilService;
import ru.practicum.ewmservice.util.mappers.CompilationsMapper;
import ru.practicum.statsclient.StatsClientImpl;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@Transactional
public class CompilationServiceImpl extends UtilService implements CompilationService{
    public CompilationServiceImpl(UserRepo userRepo,
                                  EventRepo eventRepo,
                                  CategoryRepo categoryRepo,
                                  LocationRepo locationRepo,
                                  EventStateRepo eventStateRepo,
                                  CompilationRepo compilationRepo,
                                  EventRequestRepo eventRequestRepo,
                                  EventRequestStatsRepo eventRequestStatsRepo,
                                  StatsClientImpl statsClient) {
        super(userRepo, eventRepo, categoryRepo, locationRepo, eventStateRepo,
                compilationRepo, eventRequestRepo, eventRequestStatsRepo, statsClient);
    }

    @Override
    public CompilationDto create(CompilationIncomeDto dto) {
        Compilation compilation = CompilationsMapper.toCompilation(dto);
        compilation.setEvents(findEvensByIds(dto.getEventIds()));

        compilation = compilationRepo.save(compilation);
        log.info("Создана подборка c id = {} ", compilation.getId());

        return CompilationsMapper.toCompilationDto(compilation);
    }

    @Override
    public CompilationDto update(CompilationIncomeDto dto, long compId) {
        Compilation compilation = findCompilationOrThrow(compId);

        if (dto.getPinned() != null) compilation.setPinned(dto.getPinned());
        if (dto.getTitle() != null) compilation.setTitle(dto.getTitle());
        if (!dto.getEventIds().isEmpty()) {
            compilation.setEvents(findEvensByIds(dto.getEventIds()));
        }

        return CompilationsMapper.toCompilationDto(compilation);
    }

    @Override
    public CompilationDto delete(long compId) {
        Compilation compilation = findCompilationOrThrow(compId);

        compilationRepo.delete(compilation);
        log.info("Удалена подборка c id = {} ", compilation.getId());

        return CompilationsMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAll(boolean pinned, int from, int size) {
        List<Compilation> compilations;
        Pageable pageable = createPageable("id", from, size);

        compilations = findByPinned(pinned, pageable);
        log.info("Возвращаю коллекцию подборок событий по запросу");

        return CompilationsMapper.toCompilationDto(compilations);
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getById(long compId) {
        Compilation compilation = findCompilationOrThrow(compId);
        log.info("Возвращаю подборку c id = {} ", compilation.getId());

        return CompilationsMapper.toCompilationDto(compilation);
    }

    private List<Compilation> findByPinned(boolean pinned, Pageable pageable) {
        return compilationRepo.findAllByPinned(pinned, pageable);
    }

    private Set<Event> findEvensByIds(List<Long> ids) {
        return eventRepo.findByIdIn(ids);
    }
}
