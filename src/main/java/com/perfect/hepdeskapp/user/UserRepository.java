package com.perfect.hepdeskapp.user;

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
    @Query("SELECT u FROM User u JOIN Role r WHERE r.id = :roleid")
    public List<User> findUserByRole(@Param("roleid") Long roleid);
    @Query("select u FROM User u JOIN Department d WHERE d.id = :departmentid")
    public List<User> findUserByDepartment(@Param("departmentid") Long departmanetid);

}
