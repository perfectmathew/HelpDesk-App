package com.perfect.hepdeskapp.task;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository  extends JpaRepository<Task, Long> {
}
