package ru.practicum.statsdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Stat {
    private final String app;
    private final String uri;
    private final Integer hits;
}
