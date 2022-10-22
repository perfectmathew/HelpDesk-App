package com.perfect.hepdeskapp.status;

import javax.persistence.*;

@Entity
@Table(name = "status",schema = "public")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatuss(String status) {
        this.status = status;
    }
}
