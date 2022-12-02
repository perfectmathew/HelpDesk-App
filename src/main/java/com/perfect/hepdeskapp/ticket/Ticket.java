package com.perfect.hepdeskapp.ticket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.perfect.hepdeskapp.priority.Priority;
import com.perfect.hepdeskapp.attachment.Attachment;
import com.perfect.hepdeskapp.department.Department;
import com.perfect.hepdeskapp.status.Status;
import com.perfect.hepdeskapp.task.Task;
import com.perfect.hepdeskapp.user.User;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
    private String notifier_name;
    @NotNull
    private String notifier_surname;
    @NotNull
    private String notifier_phonenumber;
    @NotNull
    private String notifier_email;
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

    public String getNotifier_name() {
        return notifier_name;
    }

    public void setNotifier_name(String notifier_name) {
        this.notifier_name = notifier_name;
    }

    public String getNotifier_surname() {
        return notifier_surname;
    }

    public void setNotifier_surname(String notifier_surname) {
        this.notifier_surname = notifier_surname;
    }

    public String getNotifier_phonenumber() {
        return notifier_phonenumber;
    }

    public void setNotifier_phonenumber(String notifier_phonenumber) {
        this.notifier_phonenumber = notifier_phonenumber;
    }

    public String getNotifier_email() {
        return notifier_email;
    }

    public void setNotifier_email(String notifier_email) {
        this.notifier_email = notifier_email;
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
}
