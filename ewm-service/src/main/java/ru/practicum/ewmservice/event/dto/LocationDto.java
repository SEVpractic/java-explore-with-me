package ru.practicum.ewmservice.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
public class LocationDto {
    private double lat;
    private double lon;
}
