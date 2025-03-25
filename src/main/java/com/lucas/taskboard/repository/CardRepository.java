package com.lucas.taskboard.repository;

import com.lucas.taskboard.model.TaskCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<TaskCard, Long> {
}
