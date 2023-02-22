package ru.practicum.ewmservice.util.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmservice.event.dto.EventIncomeDto;
import ru.practicum.ewmservice.event.dto.LocationDto;
import ru.practicum.ewmservice.event.model.Location;

@UtilityClass
public class LocationMapper {
    public static Location toLocation(EventIncomeDto dto) {
        Location location = new Location();

        location.setLat(dto.getLocation().getLat());
        location.setLon(dto.getLocation().getLon());

        return location;
    }

    public static LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lon(location.getLon())
                .lat(location.getLat())
                .build();
    }
}
