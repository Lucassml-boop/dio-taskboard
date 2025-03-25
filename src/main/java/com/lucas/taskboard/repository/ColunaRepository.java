package com.lucas.taskboard.repository;

import com.lucas.taskboard.model.Coluna;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColunaRepository extends JpaRepository<Coluna, Long> {
}
