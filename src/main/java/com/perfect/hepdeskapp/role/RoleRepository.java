package com.perfect.hepdeskapp.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    @Query("SELECT r FROM Role r WHERE r.id = :id")
    public Role findRoleById(@Param("id") Long id);
    @Query("select r from Role r WHERE r.name = :name")
    public Role findRoleByName(@Param("name") String name);
}
