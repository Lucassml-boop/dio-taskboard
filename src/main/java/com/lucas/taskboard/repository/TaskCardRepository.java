package com.lucas.taskboard.repository;

import com.lucas.taskboard.model.TaskCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskCardRepository extends JpaRepository<TaskCard, Long> {
}
