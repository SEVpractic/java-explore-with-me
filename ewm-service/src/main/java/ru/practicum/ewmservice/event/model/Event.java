package ru.practicum.ewmservice.event.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewmservice.categories.model.Category;
import ru.practicum.ewmservice.compilation.model.Compilation;
import ru.practicum.ewmservice.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "events")
@Setter
@Getter
public class Event {
    @Id
    @Column(name = "event_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "annotation")
    private String annotation; // Краткое описание
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(name = "created_on")
    private LocalDateTime createdOn; // Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")
    @Column(name = "description")
    private String description; // Полное описание события
    @Column(name = "event_date")
    private LocalDateTime eventDate; // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "initiator_id")
    private User initiator; // Создатель
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Location location; //Координаты места проведения
    @Column(name = "paid")
    private Boolean paid; // Нужно ли оплачивать участие
    @Column(name = "participant_limit")
    private Integer participantLimit; // Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    @Column(name = "published_on")
    private LocalDateTime publishedOn; // Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")
    @Column(name = "request_moderation")
    private Boolean requestModeration; // Нужна ли пре-модерация заявок на участие
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "state_id")
    private EventState state;// Список состояний жизненного цикла события
    @Column(name = "title")
    private String title; // Заголовок
    @ManyToMany(mappedBy = "events")
    private Set<Compilation> compilations; // Подборки
}
