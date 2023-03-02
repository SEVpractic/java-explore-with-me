package ru.practicum.ewmservice.participation_request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmservice.participation_request.model.EventRequestStat;

import java.util.Optional;

public interface EventRequestStatsRepo extends JpaRepository<EventRequestStat, Long> {
    Optional<EventRequestStat> findByName(String name);
}
