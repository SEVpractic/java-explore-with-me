package ru.practicum.ewmservice.compilation.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmservice.compilation.model.Compilation;

public interface CompilationRepo extends JpaRepository<Compilation, Long> {
    Page<Compilation> findAllByPinned(boolean pinned, Pageable pageable);
    Page<Compilation> findAll(Pageable pageable);
}
