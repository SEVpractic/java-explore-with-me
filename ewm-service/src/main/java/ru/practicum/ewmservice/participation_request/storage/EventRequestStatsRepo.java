package ru.practicum.ewmservice.participation_request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.participation_request.model.EventRequestStat;

import java.util.Optional;

@Repository
public interface EventRequestStatsRepo extends JpaRepository<EventRequestStat, Long> {
    Optional<EventRequestStat> findByName(String name);
}
