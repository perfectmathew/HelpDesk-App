package com.perfect.hepdeskapp.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.perfect.hepdeskapp.department.Department;
import com.perfect.hepdeskapp.role.Role;
import com.perfect.hepdeskapp.ticket.Ticket;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user",schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotBlank
    private String surname;
    @NotBlank
    private String phone_number;
    @Column(unique = true)
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private boolean enabled;
    private String password_token;
    @ManyToOne
    @JoinColumn(name = "departmentid")
    private Department department;
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )

    private Set<Role> roleSet = new HashSet<>();
    @JsonIgnore
    @ManyToMany(mappedBy = "userList")
    private List<Ticket> userTickets = new ArrayList<>();


    public User(){

    }
    public User(String name, String surname, String phone_number, String email, String password, Department department, Role role) {
        this.name = name;
        this.surname = surname;
        this.phone_number = phone_number;
        this.email = email;
        this.password = password;
        this.department = department;
        this.roleSet.add(role);
    }
    public User(String name, String surname, String phone_number, String email, Department department, Role role) {
        this.name = name;
        this.surname = surname;
        this.phone_number = phone_number;
        this.email = email;
        this.department = department;
        this.roleSet.add(role);
    }
    public User(String name, String surname, String phone_number, String email,String password, Department department) {
        this.name = name;
        this.surname = surname;
        this.phone_number = phone_number;
        this.email = email;
        this.password = password;
        this.department = department;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Set<Role> getRoleSet() {
        return roleSet;
    }

    public void setRoleSet(Role role) {
        this.roleSet.add(role);
    }
    public void removeRole(Role role) { this.roleSet.remove(role); }

    public String getPassword_token() {
        return password_token;
    }

    public void setPassword_token(String password_token) {
        this.password_token = password_token;
    }

    public List<Ticket> getUserTickets() {
        return userTickets;
    }

    public void addTicketToUser(Ticket ticket) {
        this.userTickets.add(ticket);
    }
    public void removeTicketFromUser(Ticket ticket){
        this.userTickets.remove(ticket);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


}
