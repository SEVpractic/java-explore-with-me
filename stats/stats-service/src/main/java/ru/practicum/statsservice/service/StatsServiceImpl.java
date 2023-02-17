package ru.practicum.statsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.statsdto.dto.HitDto;
import ru.practicum.statsdto.dto.Stat;
import ru.practicum.statsservice.storage.StatsRepo;
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
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {
    private final StatsRepo statsRepo;

    @Override
    @Transactional
    public void saveRequest(HitDto dto) {
        statsRepo.save(HitMapper.toHit(dto));
        log.info("сохранен запрос ip = {} по url = {}", dto.getIp(), dto.getUri());
    }

    @Override
    @SneakyThrows
    public List<Stat> getHits(String start, String end, List<String> uris, boolean unique) {
        List<Stat> stat;

        LocalDateTime startTime = decodeAndParse(start);
        LocalDateTime endTime = decodeAndParse(end);
        checkPeriod(startTime, endTime);

        if (unique) {
            stat = getUniqueIpStat(startTime, endTime, uris);
            log.info("сформирована статистика запросов по uris ={} для уникальных ip", uris);
        } else {
            stat = getNotUniqueIpStat(startTime, endTime, uris);
            log.info("сформирована статистика запросов по uris ={} для не уникальных ip", uris);
        }

        return stat;
    }

    private List<Stat> getNotUniqueIpStat(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return statsRepo.getNotUniqueIpStat(start, end, uris);
    }

    private List<Stat> getUniqueIpStat(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return statsRepo.getUniqueIpStat(start, end, uris);
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
