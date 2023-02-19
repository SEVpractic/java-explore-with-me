package ru.practicum.statsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.statsdto.dto.HitDto;
import ru.practicum.statsdto.dto.HitOutputDto;
import ru.practicum.statsdto.dto.Stat;
import ru.practicum.statsservice.model.App;
import ru.practicum.statsservice.model.Hit;
import ru.practicum.statsservice.storage.AppRepo;
import ru.practicum.statsservice.storage.StatsRepo;
import ru.practicum.statsservice.util.AppMapper;
import ru.practicum.statsservice.util.HitMapper;
import ru.practicum.statsservice.util.exception.InvalidPeriodException;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class StatsServiceImpl implements StatsService {
    private final AppRepo appRepo;
    private final StatsRepo statsRepo;

    @Override
    public HitOutputDto saveRequest(HitDto dto) {
        App app = getOrCreate(dto);

        Hit hit = statsRepo.save(HitMapper.toHit(dto, app));
        log.info("сохранен запрос ip = {} по url = {}", dto.getIp(), dto.getUri());

        return HitMapper.toOutputDto(hit);
    }

    @Override
    public List<Stat> getHits(String start, String end, List<String> uris, boolean unique) {
        List<Stat> stat;

        LocalDateTime startTime = decodeAndParse(start);
        LocalDateTime endTime = decodeAndParse(end);
        checkPeriod(startTime, endTime);

        stat = get(startTime, endTime, uris, unique);



        return stat;
    }

    private App getOrCreate(HitDto dto) {
        return appRepo.findByName(dto.getApp())
                .orElseGet(() -> appRepo.save(AppMapper.toApp(dto)));
    }

    private List<Stat> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (uris.stream().anyMatch(uri -> uri.equals("/events"))) {
            if (unique) {
                log.info("сформирована статистика запросов по uris ={} для уникальных ip", uris);
                return statsRepo.getUniqueIpStatNoUri(start, end);
            } else {
                log.info("сформирована статистика запросов по uris ={} для не уникальных ip", uris);
                return statsRepo.getNotUniqueIpStatNoUri(start, end);
            }
        } else {
            if (unique) {
                log.info("сформирована статистика запросов по uris ={} для уникальных ip", uris);
                return statsRepo.getUniqueIpStat(start, end, uris);
            } else {
                log.info("сформирована статистика запросов по uris ={} для не уникальных ip", uris);
                return statsRepo.getNotUniqueIpStat(start, end, uris);
            }
        }
    }

    private LocalDateTime decodeAndParse(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String decodeTime = URLDecoder.decode(time, StandardCharsets.UTF_8);

        return LocalDateTime.parse(decodeTime, formatter);
    }

    private void checkPeriod(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new InvalidPeriodException("Конец периода поиска не может быть раньше начала");
        }
    }
}
