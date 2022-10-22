package com.perfect.hepdeskapp.department;

import com.perfect.hepdeskapp.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department,Long> {
    @Query("SELECT d FROM Department d WHERE d.name = :name")
    public Department findDepartmentByName(@Param("name") String name);
    @Query("select d FROM Department d WHERE d.id = :id")
    public Department findDepartmentById(@Param("id") Long id);
}
