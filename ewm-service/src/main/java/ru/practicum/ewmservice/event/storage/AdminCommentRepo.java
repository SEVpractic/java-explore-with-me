package ru.practicum.ewmservice.event.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmservice.event.model.AdminComment;
import ru.practicum.ewmservice.event.model.Event;

import java.util.List;

public interface AdminCommentRepo extends JpaRepository<AdminComment, Long> {
    @Query("select c from AdminComment as c " +
            "where c.event in :events " +
            "and c.createdOn = ( " +
            "select max(ac.createdOn) from AdminComment as ac " +
            "where ac.event = c.event " +
            ")")
    List<AdminComment> findLastByEventIds(List<Event> events);
}
