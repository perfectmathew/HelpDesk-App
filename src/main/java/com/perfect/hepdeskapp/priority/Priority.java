package com.perfect.hepdeskapp.priority;

import javax.persistence.*;

@Entity
@Table(name = "priority",schema = "public")
public class Priority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String priority_name;
    private long priority_value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPriority_name() {
        return priority_name;
    }

    public void setPriority_name(String priority_name) {
        this.priority_name = priority_name;
    }

    public long getPriority_value() {
        return priority_value;
    }

    public void setPriority_value(long priority_value) {
        this.priority_value = priority_value;
    }
}
