package com.perfect.hepdeskapp.solutions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolutionRepository extends JpaRepository<Solution,Long> {
    @Query("SELECT s FROM Solution s WHERE s.id = :id")
    public Solution findSolutionById(@Param("id") Long Id);
    @Query("SELECT s FROM Solution s WHERE s.solution LIKE %:text%")
    public List<Solution> findSolutionsByText(@Param("text") String search_term);
}
