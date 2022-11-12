package com.perfect.hepdeskapp.user;

import com.perfect.hepdeskapp.department.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Query("SELECT u FROM User u WHERE u.email = :email")
    public User findUserByEmail(@Param("email") String email);
    @Query("SELECT u FROM User u WHERE u.password_token = :token")
    public User findUserByPassword_token(@Param("token") String token);
    @Query("SELECT u FROM User u WHERE u.id = :userid")
    public User findUserById(@Param("userid") Long userid);
    @Query("SELECT u FROM User u JOIN Role r WHERE r.id = :roleid")
    public List<User> findUserByRoleId(@Param("roleid") Long roleid);
    @Query("select u FROM User u JOIN Department d WHERE d.id = :departmentid")
    public List<User> findUserByDepartment(@Param("departmentid") Long departmanetid);
    @Query("select DISTINCT u FROM User u JOIN u.roleSet rs join rs.userSet  WHERE u.department = :department and rs.name = :role_name")
    public List<User> findUserByDepartmentAndRole(@Param("department") Department department, @Param("role_name") String role_name);
    @Query("SELECT DISTINCT u FROM User u JOIN u.roleSet rs JOIN rs.userSet WHERE rs.name = :roleName")
    public List<User> findUserByRoleName(@Param("roleName") String roleName);
}
