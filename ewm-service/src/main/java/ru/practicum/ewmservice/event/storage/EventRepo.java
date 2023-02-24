package ru.practicum.ewmservice.event.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.users.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepo extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiator(User initiator);

    @Query("select e from Event as e " +
            "where e.id = :eventId and e.state.id = 2")
    Optional<Event> findPublicById(long eventId);

    @Query("select e from Event as e " +
            "where e.paid = :paid and e.category.id in :categories " +
            "and e.state.id = 2" +
            "and e.eventDate between :rangeStart and :rangeEnd " +
            "and e.confirmedRequests < e.participantLimit " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findAvailableEventsByDateRange(String text, List<Long> categories, boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Pageable pageable);

    @Query("select e from Event as e " +
            "where e.paid = :paid and e.category.id in :categories " +
            "and e.state.id = 2" +
            "and e.eventDate between :rangeStart and :rangeEnd " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findEventsByDateRange(String text, List<Long> categories, boolean paid,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                      Pageable pageable);

    @Query("select e from Event as e " +
            "where e.paid = :paid and e.category.id in :categories " +
            "and e.state.id = 2" +
            "and e.eventDate > :rangeStart " +
            "and e.confirmedRequests < e.participantLimit " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findAvailableEventsByStartDate(String text, List<Long> categories, boolean paid,
                                               LocalDateTime rangeStart, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.paid = :paid and e.category.id in :categories " +
            "and e.state.id = 2" +
            "and e.eventDate > :rangeStart " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findEventsByStartDate(String text, List<Long> categories, boolean paid,
                                      LocalDateTime rangeStart, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.paid = :paid and e.category.id in :categories " +
            "and e.state.id = 2" +
            "and e.eventDate > current_timestamp " +
            "and e.confirmedRequests < e.participantLimit " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findAvailableEventsFromNow(String text, List<Long> categories, boolean paid, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.paid = :paid and e.category.id in :categories " +
            "and e.state.id = 2" +
            "and e.eventDate > current_timestamp " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findEventsFromNow(String text, List<Long> categories, boolean paid, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.initiator.id in :userIds " +
            "and e.state.name in :states " +
            "and e.category.id in :categories " +
            "and e.eventDate between :rangeStart and :rangeEnd ")
    List<Event> findEventsByUsersAndDateRange(List<Long> userIds, List<String> states, List<Long> categories,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.initiator.id in :userIds " +
            "and e.state.name in :states " +
            "and e.category.id in :categories " +
            "and e.eventDate > :rangeStart ")
    List<Event> findEventsByUsersStartDate(List<Long> userIds, List<String> states, List<Long> categories,
                                           LocalDateTime rangeStart, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.initiator.id in :userIds " +
            "and e.state.name in :states " +
            "and e.category.id in :categories " +
            "and e.eventDate > current_timestamp ")
    List<Event> findEventsByUsers(List<Long> userIds, List<String> states, List<Long> categories,
                                  Pageable pageable);
}
