package ru.practicum.ewmservice.event.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewmservice.categories.model.Category;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepo extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    List<Event> findAllByInitiator(User initiator);

    @Query("select e from Event as e " +
            "where e.id = :eventId and e.state.id = 2")
    Optional<Event> findPublicById(long eventId);

    Set<Event> findByIdIn(List<Long> ids);

    List<Event> findAllByCategory(Category category);
}
