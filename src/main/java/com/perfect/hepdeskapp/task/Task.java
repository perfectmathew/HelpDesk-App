package com.perfect.hepdeskapp.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.perfect.hepdeskapp.ticket.Ticket;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "task", schema = "public")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String task;
    private String description;
    private boolean done;
    @ManyToMany(mappedBy = "ticketTasksSet")
    @JsonIgnore
    public Set<Ticket> ticketSet = new HashSet<>();
    public Task(){

    }
    public Task(String task, String description, boolean done){
        this.task = task;
        this.description = description;
        this.done = done;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
