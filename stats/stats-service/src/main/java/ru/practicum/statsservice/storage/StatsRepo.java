package ru.practicum.statsservice.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.statsdto.Stat;
import ru.practicum.statsservice.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepo extends JpaRepository<Hit, Long> {
    @Query(name = "GetNotUniqueIpStat", nativeQuery = true)
    List<Stat> getNotUniqueIpStat(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(name = "GetUniqueIpStat", nativeQuery = true)
    List<Stat> getUniqueIpStat(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(name = "GetNotUniqueIpStatNoUri", nativeQuery = true)
    List<Stat> getNotUniqueIpStatNoUri(LocalDateTime start, LocalDateTime end);

    @Query(name = "GetNotUniqueIpStatNoUri", nativeQuery = true)
    List<Stat> getUniqueIpStatNoUri(LocalDateTime start, LocalDateTime end);
}
