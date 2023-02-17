package ru.yandex.statsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.statsservice.dto.HitDto;
import ru.yandex.statsservice.dto.Stat;
import ru.yandex.statsservice.storage.StatsRepo;
import ru.yandex.statsservice.util.HitMapper;

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

        if (unique) {
            stat = getUniqueIpStat(startTime, endTime, uris);
        } else {
            stat = getNotUniqueIpStat(startTime, endTime, uris);
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

        //String encodeStart = URLEncoder.encode(start, StandardCharsets.UTF_8); // todo реализовать декодирование

        return LocalDateTime.parse(time, formatter);
    }
}
