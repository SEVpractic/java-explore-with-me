package ru.practicum.ewmservice.util.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.statsdto.Stat;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class ViewsMapper {

    public static Map<Long, List<Stat>> toStatsMap(List<Stat> stats) {
        return stats.stream()
                .collect(
                        Collectors.groupingBy(
                                stat -> Long.valueOf(stat.getUri().substring(8)),
                                Collectors.toList()
                        )
                );
    }
}
