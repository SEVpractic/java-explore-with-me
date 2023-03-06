package ru.practicum.ewmservice.util.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.statsdto.Stat;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@UtilityClass
public class ViewsMapper {

    public static Map<Long, Integer> toStatsMap(List<Stat> stats) {
        return stats.stream()
                .collect(
                        toMap(stat -> Long.valueOf(stat.getUri().substring(8)), Stat::getHits)
                );
    }
}
