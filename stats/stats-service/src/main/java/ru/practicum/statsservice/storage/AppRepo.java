package ru.practicum.statsservice.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.statsservice.model.App;

import java.util.Optional;

@Repository
public interface AppRepo extends JpaRepository<App, Long> {
    Optional<App> findByName(String name);
}
