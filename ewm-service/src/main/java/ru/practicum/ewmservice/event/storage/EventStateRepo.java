package ru.practicum.ewmservice.event.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.event.model.EventState;

import java.util.Optional;

@Repository
public interface EventStateRepo extends JpaRepository<EventState, Long> {
    Optional<EventState> findByName(String state);
}
