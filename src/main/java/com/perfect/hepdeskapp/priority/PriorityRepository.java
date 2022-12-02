package com.perfect.hepdeskapp.priority;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PriorityRepository extends JpaRepository<Priority, Long> {
    @Query("SELECT p FROM Priority p WHERE p.priority_name = :name")
    public Priority findPriorityByPriority_name(@Param("name") String name);
    @Query("SELECT p FROM Priority p WHERE p.id = :id")
    public Priority findPriorityById(@Param("id") Long id);
}
