package com.perfect.hepdeskapp.status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusRepository extends JpaRepository<Status,Long> {
    @Query("SELECT s FROM Status s WHERE s.status = :status")
    public Status findStatusByStatus(@Param("status") String status);
    @Query("SELECT s FROM Status s WHERE s.id = :id")
    public Status findStatusById(@Param("id") Long id);

    @Query("SELECT s FROM Status s WHERE s.status <> 'DONE' AND s.status <> 'ARCHIVED'")
    public List<Status> findStatusesByWorkerClass();
}
