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
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "event_id")
    private Event event;
    @Column(name = "created")
    private LocalDateTime createdOn; // Дата и время создания комментария
    @Column(name = "text")
    private String text; // Текст комментария
}
