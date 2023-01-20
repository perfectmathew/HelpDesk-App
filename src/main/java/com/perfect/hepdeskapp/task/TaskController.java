package com.perfect.hepdeskapp.task;

import com.perfect.hepdeskapp.config.EmailExistsException;
import com.perfect.hepdeskapp.role.Role;
import com.perfect.hepdeskapp.role.RoleRepository;
import com.perfect.hepdeskapp.ticket.Ticket;
import com.perfect.hepdeskapp.ticket.TicketRepository;
import com.perfect.hepdeskapp.user.User;
import com.perfect.hepdeskapp.user.UserDetail;
import com.perfect.hepdeskapp.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.awt.image.LookupOp;

@Controller
public class TaskController {

    final
    TicketRepository ticketRepository;
    final
    TaskRepository taskRepository;
    final UserRepository userRepository;
    final RoleRepository roleRepository;

    public TaskController(TicketRepository ticketRepository, TaskRepository taskRepository, UserRepository userRepository, RoleRepository roleRepository) {
        this.ticketRepository = ticketRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/manager/api/createTask")
    @ResponseBody
    public Task createTask(@RequestParam("ticket_id") Long ticket_id, @RequestParam("task_name") String task_name,
                           @RequestParam("task_description") String task_description){
        if(task_description.length() > 255) throw new EmailExistsException("Task description is longer than 255 letter's");
        Task task =  new Task(task_name,task_description,false);
        taskRepository.saveAndFlush(task);
        Ticket ticket = ticketRepository.findTicketById(ticket_id);
        ticket.addTaskToTicket(task);
        ticketRepository.saveAndFlush(ticket);
        return task;
    }

    @PatchMapping("/worker/api/markTaskAsDone")
    @ResponseBody
    public String markTaskAsDone(@RequestParam("task-id") Long task_id){
        Task task = taskRepository.findTaskById(task_id);
        task.setDone(true);
        taskRepository.saveAndFlush(task);
        return "Successfully";
    }

    @GetMapping("/api/getTaskInfo")
    @ResponseBody
    public Task getTaskInfo(@RequestParam("task_id") Long task_id){
        return taskRepository.findTaskById(task_id);
    }
    @PostMapping("/manager/api/updateTask")
    @ResponseBody
    public Task updateTask(@RequestParam("task_id") Long task_id,@RequestParam("task_name") String task_name, @RequestParam("task_description") String task_description, @RequestParam("task_done") boolean task_done){
        if(task_description.length() > 255) throw new EmailExistsException("Task description is longer than 255 letter's");
        Task task = taskRepository.findTaskById(task_id);
        task.setTask(task_name);
        task.setDescription(task_description);
        task.setDone(task_done);
        taskRepository.saveAndFlush(task);
        return task;
    }
    @PostMapping("/manager/api/deleteTask")
    @ResponseBody
    public String deleteTask(@RequestParam("ticket_id") Long ticket_id, @RequestParam("task_id") Long task_id){
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        User user = userRepository.findUserByEmail(((UserDetail)principal).getUsername());
        Ticket ticket =  ticketRepository.findTicketById(ticket_id);
        if (user.getRoleSet().contains(roleRepository.findRoleByName("DEPARTMENT_BOSS"))) {
            if (!ticket.getDepartment().equals(user.getDepartment())) {
                return "Insufficient authority";
            }
        }
        ticket.removeTaskFromTicket(taskRepository.findTaskById(task_id));
        ticketRepository.saveAndFlush(ticket);
        taskRepository.delete(taskRepository.findTaskById(task_id));
        return "Successful";
    }
}
