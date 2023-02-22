package ru.practicum.ewmservice.categories.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.categories.model.Category;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {
}
