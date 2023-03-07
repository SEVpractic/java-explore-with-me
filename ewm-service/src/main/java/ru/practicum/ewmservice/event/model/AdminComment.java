package ru.practicum.ewmservice.event.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_admin_comments")
@Setter
@Getter
public class AdminComment {
    @Id
    @Column(name = "admin_comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    @Column(name = "event_id")
    private Long eventId;
    @Column(name = "created")
    private LocalDateTime createdOn; // Дата и время создания комментария
    @Column(name = "text")
    private String text; // Текст комментария
}
