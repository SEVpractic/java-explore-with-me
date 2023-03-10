package ru.practicum.ewmservice.event.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmservice.event.model.AdminComment;
import ru.practicum.ewmservice.event.model.Event;

import java.util.List;

public interface AdminCommentRepo extends JpaRepository<AdminComment, Long> {

    List<AdminComment> findFirstByEventInOrderByCreatedOnDesc(List<Event> events);
}
