package ru.practicum.ewmservice.user.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "users_roles")
@Setter
@Getter
public class UserRole { // todo удалить или реализовать
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "role")
    private UserRoles role;
}
