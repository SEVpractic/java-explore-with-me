package ru.practicum.ewmservice.event.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewmservice.categories.model.Category;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepo extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    List<Event> findAllByInitiator(User initiator);

    @Query("select e from Event as e " +
            "where e.id = :eventId and e.state.id = 2")
    Optional<Event> findPublicById(long eventId);

    @Query("select e from Event as e " +
            "where e.paid = :paid and e.category.id in :categories " +
            "and e.state.id = 2" +
            "and e.eventDate between :rangeStart and :rangeEnd " +
            "and (select count(er) from EventRequest as er where er.event = e) < e.participantLimit " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findAvailableEventsByDateRange(String text, List<Long> categories, boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Pageable pageable);

    @Query("select e from Event as e " +
            "where e.category.id in :categories " +
            "and e.state.id = 2" +
            "and e.eventDate between :rangeStart and :rangeEnd " +
            "and (select count(er) from EventRequest as er where er.event = e) < e.participantLimit " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findAvailableEventsByDateRange(String text, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Pageable pageable);

    @Query("select e from Event as e " +
            "where e.paid = :paid " +
            "and e.state.id = 2" +
            "and e.eventDate between :rangeStart and :rangeEnd " +
            "and (select count(er) from EventRequest as er where er.event = e) < e.participantLimit " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findAvailableEventsByDateRange(String text, boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Pageable pageable);

    @Query("select e from Event as e " +
            "where e.state.id = 2" +
            "and e.eventDate between :rangeStart and :rangeEnd " +
            "and (select count(er) from EventRequest as er where er.event = e) < e.participantLimit " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findAvailableEventsByDateRange(String text,
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
            "where e.paid = :paid " +
            "and e.state.id = 2" +
            "and e.eventDate between :rangeStart and :rangeEnd " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findEventsByDateRange(String text, boolean paid,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                      Pageable pageable);

    @Query("select e from Event as e " +
            "where e.category.id in :categories " +
            "and e.state.id = 2" +
            "and e.eventDate between :rangeStart and :rangeEnd " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findEventsByDateRange(String text, List<Long> categories,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                      Pageable pageable);

    @Query("select e from Event as e " +
            "where e.state.id = 2" +
            "and e.eventDate between :rangeStart and :rangeEnd " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findEventsByDateRange(String text,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                      Pageable pageable);

    @Query("select e from Event as e " +
            "where e.paid = :paid and e.category.id in :categories " +
            "and e.state.id = 2" +
            "and e.eventDate > :rangeStart " +
            "and (select count(er) from EventRequest as er where er.event = e) < e.participantLimit " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findAvailableEventsByStartDate(String text, List<Long> categories, boolean paid,
                                               LocalDateTime rangeStart, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.category.id in :categories " +
            "and e.state.id = 2" +
            "and e.eventDate > :rangeStart " +
            "and (select count(er) from EventRequest as er where er.event = e) < e.participantLimit " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findAvailableEventsByStartDate(String text, List<Long> categories,
                                               LocalDateTime rangeStart, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.paid = :paid " +
            "and e.state.id = 2" +
            "and e.eventDate > :rangeStart " +
            "and (select count(er) from EventRequest as er where er.event = e) < e.participantLimit " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findAvailableEventsByStartDate(String text, boolean paid,
                                               LocalDateTime rangeStart, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.state.id = 2" +
            "and e.eventDate > :rangeStart " +
            "and (select count(er) from EventRequest as er where er.event = e) < e.participantLimit " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findAvailableEventsByStartDate(String text,
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
            "where e.paid = :paid " +
            "and e.state.id = 2" +
            "and e.eventDate > :rangeStart " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findEventsByStartDate(String text, boolean paid,
                                      LocalDateTime rangeStart, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.category.id in :categories " +
            "and e.state.id = 2" +
            "and e.eventDate > :rangeStart " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findEventsByStartDate(String text, List<Long> categories,
                                      LocalDateTime rangeStart, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.state.id = 2" +
            "and e.eventDate > :rangeStart " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findEventsByStartDate(String text,
                                      LocalDateTime rangeStart, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.paid = :paid and e.category.id in :categories " +
            "and e.state.id = 2" +
            "and e.eventDate > current_timestamp " +
            "and (select count(er) from EventRequest as er where er.event = e) < e.participantLimit " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findAvailableEventsFromNow(String text, List<Long> categories, boolean paid, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.category.id in :categories " +
            "and e.state.id = 2" +
            "and e.eventDate > current_timestamp " +
            "and (select count(er) from EventRequest as er where er.event = e) < e.participantLimit " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findAvailableEventsFromNow(String text, List<Long> categories, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.paid = :paid " +
            "and e.state.id = 2" +
            "and e.eventDate > current_timestamp " +
            "and (select count(er) from EventRequest as er where er.event = e) < e.participantLimit " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findAvailableEventsFromNow(String text, boolean paid, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.state.id = 2" +
            "and e.eventDate > current_timestamp " +
            "and (select count(er) from EventRequest as er where er.event = e) < e.participantLimit " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findAvailableEventsFromNow(String text, Pageable pageable);

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
            "where e.paid = :paid " +
            "and e.state.id = 2" +
            "and e.eventDate > current_timestamp " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findEventsFromNow(String text, boolean paid, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.category.id in :categories " +
            "and e.state.id = 2" +
            "and e.eventDate > current_timestamp " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findEventsFromNow(String text, List<Long> categories, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.state.id = 2" +
            "and e.eventDate > current_timestamp " +
            "and (" +
            "lower(e.annotation) like concat('%', lower(:text) , '%') " +
            "or lower(e.description) like concat('%', lower(:text) , '%') " +
            ")")
    List<Event> findEventsFromNow(String text, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.initiator.id in :userIds " +
            "and e.state.name in :states " +
            "and e.category.id in :categories " +
            "and e.eventDate between :rangeStart and :rangeEnd ")
    List<Event> findAllByUsersAndCategoriesAndDateRange(List<Long> userIds, List<String> states,
                                                        List<Long> categories,
                                                        LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                        Pageable pageable);

    @Query("select e from Event as e " +
            "where e.state.name in :states " +
            "and e.eventDate between :rangeStart and :rangeEnd ")
    List<Event> findAllByDateRange(List<String> states, LocalDateTime rangeStart,
                                   LocalDateTime rangeEnd, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.state.name in :states " +
            "and e.category.id in :categories " +
            "and e.eventDate between :rangeStart and :rangeEnd ")
    List<Event> findAllByCategoriesAndDateRange(List<String> states, List<Long> categories,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.initiator.id in :userIds " +
            "and e.state.name in :states " +
            "and e.eventDate between :rangeStart and :rangeEnd ")
    List<Event> findAllByUsersAndDateRange(List<Long> userIds, List<String> states, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.initiator.id in :userIds " +
            "and e.state.name in :states " +
            "and e.category.id in :categories " +
            "and e.eventDate > :rangeStart ")
    List<Event> findAllByUsersAndCategoriesAndStartDate(List<Long> userIds, List<String> states, List<Long> categories,
                                                        LocalDateTime rangeStart, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.state.name in :states " +
            "and e.category.id in :categories " +
            "and e.eventDate > :rangeStart ")
    List<Event> findAllByCategoriesAndStartDate(List<String> states, List<Long> categories,
                                                LocalDateTime rangeStart, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.initiator.id in :userIds " +
            "and e.state.name in :states " +
            "and e.eventDate > :rangeStart ")
    List<Event> findAllByUsersAndStartDate(List<Long> userIds, List<String> states,
                                           LocalDateTime rangeStart, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.state.name in :states " +
            "and e.eventDate > :rangeStart ")
    List<Event> findAllByStartDate(List<String> states, LocalDateTime rangeStart, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.initiator.id in :userIds " +
            "and e.state.name in :states " +
            "and e.category.id in :categories " +
            "and e.eventDate > current_timestamp ")
    List<Event> findAllByUsersAndCategories(List<Long> userIds, List<String> states, List<Long> categories,
                                            Pageable pageable);

    @Query("select e from Event as e " +
            "where e.state.name in :states " +
            "and e.eventDate > current_timestamp ")
    List<Event> findAllByNow(List<String> states, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.state.name in :states " +
            "and e.category.id in :categories " +
            "and e.eventDate > current_timestamp ")
    List<Event> findAllByCategories(List<String> states, List<Long> categories, Pageable pageable);

    @Query("select e from Event as e " +
            "where e.initiator.id in :userIds " +
            "and e.state.name in :states " +
            "and e.eventDate > current_timestamp ")
    List<Event> findAllByUsers(List<Long> userIds, List<String> states, Pageable pageable);

    Set<Event> findByIdIn(List<Long> ids);

    List<Event> findAllByCategory(Category category);
}
