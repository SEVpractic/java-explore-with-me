package ru.practicum.ewmservice.categories.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmservice.categories.model.Category;

public interface CategoryRepo extends JpaRepository<Category, Long> {
}
