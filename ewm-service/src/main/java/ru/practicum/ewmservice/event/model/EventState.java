package ru.practicum.ewmservice.event.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "event_states")
@Setter
@Getter
public class EventState {
    @Id
    @Column(name = "state_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
}
