package com.perfect.hepdeskapp.ticket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.perfect.hepdeskapp.documentation.Documentation;
import com.perfect.hepdeskapp.priority.Priority;
import com.perfect.hepdeskapp.attachment.Attachment;
import com.perfect.hepdeskapp.department.Department;
import com.perfect.hepdeskapp.solutions.Solution;
import com.perfect.hepdeskapp.status.Status;
import com.perfect.hepdeskapp.task.Task;
import com.perfect.hepdeskapp.user.User;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.sql.Timestamp;
import java.util.*;

@Entity
@Table(name = "ticket", schema = "public")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String description;
    @NotNull
    private String access_token;
    private Timestamp ticket_time;
    @ManyToOne
    @JoinColumn(name = "priority")
    private Priority priority;
    @ManyToOne
    @JoinColumn(name = "statusid")
    private Status status;
    @ManyToOne
    @JoinColumn(name = "departmentid")
    private Department department;
    @ManyToOne
    @NotNull
    @JoinColumn(name = "user_id")
    private User notifier;
    @ManyToMany
    @JoinTable(
            name = "ticket_attachments",
            joinColumns = @JoinColumn(name =  "ticket_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private List<Attachment> attachmentSet = new ArrayList<>();
    @ManyToMany
    @JoinTable(
            name = "ticket_tasks",
            joinColumns = @JoinColumn(name = "ticket_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id")
    )
    private Set<Task> ticketTasksSet = new HashSet<>();
    @JsonIgnore
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_tickets",
            joinColumns = @JoinColumn(name = "ticket_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> userList = new ArrayList<>();
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "solutionId")
    @JsonIgnore
    private Solution solution;
    public Ticket(){

    }
    public Ticket(String description, String access_token, Timestamp ticket_time, Priority priority, Status status, Department department, User notifier) {
        this.description = description;
        this.access_token = access_token;
        this.ticket_time = ticket_time;
        this.priority = priority;
        this.status = status;
        this.department = department;
        this.notifier = notifier;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public Date getTicket_time() {
        return ticket_time;
    }

    public void setTicket_time(Timestamp ticket_time) {
        this.ticket_time = ticket_time;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<Attachment> getAttachmentSet() {
        return attachmentSet;
    }

    public List<User> getUserList() {
        return userList;
    }
    public void addUserToTicket(User user){
        this.userList.add(user);
    }
    public void deleteUserFromTicket(User user) { this.userList.remove(user); }
    public Set<Task> getTicketTasksSet() {
        return ticketTasksSet;
    }
    public void addTaskToTicket(Task task) {
        this.ticketTasksSet.add(task);
    }
    public void removeTaskFromTicket(Task task) {
        this.ticketTasksSet.remove(task);
    }

    public User getNotifier() {
        return notifier;
    }

    public void setNotifier(User notifier) {
        this.notifier = notifier;
    }

    public Solution getSolution() {
        return solution;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }
}
