package ru.practicum.ewmservice.participation_request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.participation_request.model.EventRequest;
import ru.practicum.ewmservice.user.model.User;

import java.util.List;

@Repository
public interface EventRequestRepo extends JpaRepository<EventRequest, Long> {

    @Query("select count(e) from EventRequest as e " +
            "where e.requester.id = :userId and e.event.id = :eventId")
    int checkRequest(long userId, long eventId);

    List<EventRequest> findAllByRequester(User requester);

    List<EventRequest> findAllByEvent(Event event);

    List<EventRequest> findAllByIdIn(List<Long> requestIds);
}
