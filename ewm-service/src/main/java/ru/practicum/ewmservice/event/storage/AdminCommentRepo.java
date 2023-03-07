package ru.practicum.ewmservice.event.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmservice.event.model.AdminComment;

public interface AdminCommentRepo extends JpaRepository<AdminComment, Long> {
}
