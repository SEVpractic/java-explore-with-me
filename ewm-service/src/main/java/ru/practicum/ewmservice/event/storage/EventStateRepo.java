package ru.practicum.ewmservice.event.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmservice.event.model.EventState;

import java.util.Optional;

public interface EventStateRepo extends JpaRepository<EventState, Long> {
    Optional<EventState> findByName(String state);
}
