package ru.practicum.ewmservice.event.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.event.model.Location;

import java.util.Optional;

@Repository
public interface LocationRepo extends JpaRepository<Location, Long> {
    @Query("select l from Location as l where " +
            "l.lat = :lat and l.lon = :lon")
    Optional<Location> find(double lat, double lon);
}
