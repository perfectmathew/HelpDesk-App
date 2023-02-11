package com.perfect.hepdeskapp.role;

import com.perfect.hepdeskapp.user.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "role",schema = "public")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToMany(mappedBy = "roleSet")
    private Set<User> userSet = new HashSet<>();
    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Role(){

    }
    public Role(String name){
        this.name = name;
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
}
