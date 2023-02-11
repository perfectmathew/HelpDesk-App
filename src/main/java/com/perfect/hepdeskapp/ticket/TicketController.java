package com.perfect.hepdeskapp.ticket;

import com.perfect.hepdeskapp.attachment.AttachmentRepository;
import com.perfect.hepdeskapp.config.CustomErrorException;
import com.perfect.hepdeskapp.department.Department;
import com.perfect.hepdeskapp.department.DepartmentRepository;
import com.perfect.hepdeskapp.documentation.Documentation;
import com.perfect.hepdeskapp.documentation.DocumentationRepository;
import com.perfect.hepdeskapp.notification.NotifyService;
import com.perfect.hepdeskapp.priority.PriorityRepository;
import com.perfect.hepdeskapp.role.RoleRepository;
import com.perfect.hepdeskapp.status.StatusRepository;
import com.perfect.hepdeskapp.user.User;
import com.perfect.hepdeskapp.user.UserDetail;
import com.perfect.hepdeskapp.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@Controller
public class TicketController {
    private final TicketRepository ticketRepository;
    private final DepartmentRepository departmentRepository;
    private final StatusRepository statusRepository;
    private final AttachmentRepository attachmentRepository;
    private final DocumentationRepository documentationRepository;
    private final PriorityRepository priorityRepository;
    private final Environment environment;
    final
    UserRepository userRepository;
    final
    RoleRepository roleRepository;
    @Autowired
    NotifyService notify;
    final
    TicketService ticketService;
    public TicketController(TicketRepository ticketRepository, PriorityRepository priorityRepository,
                            DepartmentRepository departmentRepository, StatusRepository statusRepository,
                            AttachmentRepository attachmentRepository, UserRepository userRepository,
                            DocumentationRepository documentationRepository, Environment environment,
                            TicketService ticketService, RoleRepository roleRepository) {
        this.ticketRepository = ticketRepository;
        this.departmentRepository = departmentRepository;
        this.statusRepository = statusRepository;
        this.attachmentRepository = attachmentRepository;
        this.userRepository = userRepository;
        this.documentationRepository = documentationRepository;
        this.environment = environment;
        this.priorityRepository = priorityRepository;
        this.ticketService = ticketService;
        this.roleRepository = roleRepository;
    }

    // Request to ticket list
    // FOR ROLE ADMIN
    @RequestMapping(value = "/admin/api/tickets")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminTicketList(Model model){
        model.addAttribute("statusList",statusRepository.findAll());
        model.addAttribute("ticketList",ticketRepository.findAllByTicket_time());
        return "admin/ticket_list";
    }
    @RequestMapping(value = "/admin/api/history")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminHistoryTicketList(Model model){
        model.addAttribute("archive","archive");
        model.addAttribute("ticketList",ticketRepository.findAllByStatusContainsArchivedOrderByTicket_time());
        return "admin/ticket_list";
    }
    // Request to ticket list
    // FOR ROLE DEPARTMENT_BOSS
    @RequestMapping(value = "/manager/api/tickets")
    @PreAuthorize("hasRole('DEPARTMENT_BOSS')")
    public String managerTicketList(Model model){
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        String email = ((UserDetail)principal).getUsername();
        User user = userRepository.findUserByEmail(email);
        model.addAttribute("statusList",statusRepository.findAll());
        model.addAttribute("ticketList",ticketRepository.findTicketsByDepartmentAnAndStatusNotContainingArchive(user.getDepartment()));
        return "manager/ticket_list";
    }
    // Request to ticket list
    // FOR ROLE WORKER
    @RequestMapping(value = "/worker/api/tickets")
    @PreAuthorize("hasRole('WORKER')")
    public String workerTicketList(Model model){
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        String email = ((UserDetail)principal).getUsername();
        User user = userRepository.findUserByEmail(email);
        model.addAttribute("statusList",statusRepository.findStatusesByWorkerClass());
        model.addAttribute("ticketList",ticketRepository.findTicketsByUserListContaining(user.getEmail()));
        return "worker/ticket_list";
    }
    @RequestMapping(value = "/user/api/tickets")
    @PreAuthorize("hasRole('USER')")
    public String userTicketList(Model model){
        Object principal = SecurityContextHolder. getContext().getAuthentication().getPrincipal();
        String email = ((UserDetail)principal).getUsername();
        User user = userRepository.findUserByEmail(email);
        model.addAttribute("statusList",statusRepository.findAll());
        model.addAttribute("ticketList",ticketRepository.findTicketsByNotifier(user));
        return "user/ticket_list";
    }
    @RequestMapping(value = "/manager/api/history")
    public String managerHistoryTicketList(Model model){
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        String email = ((UserDetail)principal).getUsername();
        User user = userRepository.findUserByEmail(email);
        model.addAttribute("archive","archive");
        model.addAttribute("ticketList",ticketRepository.findTicketsByDepartmentAndStatusContainingArchivedOrderByTicket_time(user.getDepartment()));
        return "manager/ticket_list";
    }
    @GetMapping(value = "/admin/getAllTickets")
    @ResponseBody
    public List<Ticket> allTicketList(){
        return ticketRepository.findAll();
    }

    // Submit ticket function
    @PostMapping(value = "/user/api/sendTicket")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public Ticket submitTicket(HttpServletRequest request,
                               @RequestParam("description") String ticket_content,
                               @RequestParam("selectedDepartment") String selectedDepartment,
                               @RequestParam(value = "attachments", required = false) MultipartFile[] files) throws IOException {
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        User user = userRepository.findUserByEmail(((UserDetail)principal).getUsername());
        if (user == null) throw new CustomErrorException("Error with credentials. Please log in again and re-submit the form!");
        return ticketService.submitTicket(request,ticket_content,selectedDepartment,files,user);
    }
    
    @RequestMapping("/t/{ticketid}")
    @PreAuthorize("hasAnyRole('DEPARTMENT_BOSS','ADMIN')")
    public String ticketDetails(@PathVariable(value = "ticketid") long id,Model model){
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        User user = userRepository.findUserByEmail(((UserDetail)principal).getUsername());
        Ticket ticket = ticketRepository.findTicketById(id);
        Documentation documentation = documentationRepository.findDocumentationByTicket(ticket);
        if(user.getRoleSet().contains(roleRepository.findRoleByName("WORKER"))){
            if(!ticket.getUserList().contains(user)){
                model.addAttribute("permissionError","Insufficient authority!");
                return "index";
            }
            model.addAttribute("ticket",ticket);
            model.addAttribute("documentation",documentation);
            return "ticket/ticket_worker_view";
        }else if (user.getRoleSet().contains(roleRepository.findRoleByName("DEPARTMENT_BOSS"))){
            if(!ticket.getDepartment().equals(user.getDepartment())){
                model.addAttribute("permissionError","Insufficient authority!");
                return "index";
            }
        }

        List<User> userList = userRepository.findUserByDepartmentAndRole(ticket.getDepartment(),"WORKER");
        model.addAttribute("ticket",ticket);
        model.addAttribute("documentation",documentation);
        model.addAttribute("workerList",userList);
        return "ticket/ticket_view";
    }
    @PostMapping("/admin/ticket/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public String deleteTicket(@RequestParam("ticket_id") Long ticket_id){
        Ticket ticket = ticketRepository.findTicketById(ticket_id);
        if(!ticket.getAttachmentSet().isEmpty()) ticket.getAttachmentSet().clear();
        if (!ticket.getUserList().isEmpty()) ticket.getUserList().clear();
        Documentation documentation = documentationRepository.findDocumentationByTicket(ticket);
        if (documentation != null){
            if (!documentation.getDocumentationAttachmentsSet().isEmpty()) documentation.getDocumentationAttachmentsSet().clear();
            documentationRepository.delete(documentation);
        }
        ticketRepository.delete(ticket);
        return "Successfully";
    }
    @PostMapping("/manager/api/assignWorker")
    @PreAuthorize("hasAnyRole('DEPARTMENT_BOSS','ADMIN')")
    @ResponseBody
    public String assignWorkerToTicket(@RequestParam("user_id") Long user_id, @RequestParam("ticket_id") Long ticket_id){
        User user = userRepository.findUserById(user_id);
        Ticket ticket = ticketRepository.findTicketById(ticket_id);
         if(ticket.getUserList().contains(user)){
             return "Duplicated";
         }
        ticket.addUserToTicket(user);
        ticketRepository.saveAndFlush(ticket);
        return "Successful";
    }
    @PostMapping("/manager/api/unassignWorker")
    @PreAuthorize("hasAnyRole('DEPARTMENT_BOSS','ADMIN')")
    @ResponseBody
    public String unassignWorkerFromTicket(@RequestParam("user_id") Long user_id, @RequestParam("ticket_id") Long ticket_id){
        User user = userRepository.findUserById(user_id);
        Ticket ticket = ticketRepository.findTicketById(ticket_id);
        ticket.deleteUserFromTicket(user);
        ticketRepository.saveAndFlush(ticket);
        return "Successful";
    }
    @GetMapping("/status")
    public String ticketStatus(){
        return "ticket/ticket_status";
    }
    @GetMapping(value = "/status", params = {"ticket-id","ticket-token"})
    public String ticketStatus(@RequestParam(name = "ticket-id") Long ticketId, @RequestParam(name = "ticket-token") String ticketToken,Model model){
        model.addAttribute("ticket",ticketRepository.findTicketByIdAndAccess_token(ticketId,ticketToken));
        return "ticket/ticket_status";
    }
    @PostMapping("/admin/ticket/department/edit")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public String changeTicketDepartment(@RequestParam("department_id") Long department_id, @RequestParam("ticket_id") Long ticket_id){
        Ticket ticket = ticketRepository.findTicketById(ticket_id);
        Department department = departmentRepository.findDepartmentById(department_id);
        ticket.setDepartment(department);
        ticketRepository.saveAndFlush(ticket);
        return ticket.getDepartment().getName();
    }
    @PostMapping("/manager/api/changeTicketPriority")
    @PreAuthorize("hasAnyRole('DEPARTMENT_BOSS','ADMIN')")
    @ResponseBody
    public String changeTicketPriority(@RequestParam("ticket-id") Long ticket_id, @RequestParam("priority-id") Long priority_id){
        Ticket ticket = ticketRepository.findTicketById(ticket_id);
        ticket.setPriority(priorityRepository.findPriorityById(priority_id));
        ticketRepository.saveAndFlush(ticket);
        return ticket.getPriority().getPriority_name();
    }

    @GetMapping("/api/findTicketsBy")
    @ResponseBody
    public List<Ticket> findTicketBy(@RequestParam(value = "sortDirection", required = false) String sortDirection, @RequestParam("findBy") String findBy, @RequestParam("findValue") String findVal){
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        User user = userRepository.findUserByEmail(((UserDetail)principal).getUsername());
      List<Ticket> ticketList = null;
        switch (findBy) {
            case "status" -> {
                switch (findVal) {
                    case "NEW" -> {
                        if (sortDirection == null || sortDirection.isEmpty()) {
                            if (user.getRoleSet().contains(roleRepository.findRoleByName("WORKER"))) {
                                ticketList = ticketRepository.findTicketsByStatusAndUserListContaining(user.getEmail(), "NEW", Sort.by(Sort.Direction.ASC, "priority"));
                            } else if (user.getRoleSet().contains(roleRepository.findRoleByName("DEPARTMENT_BOSS"))) {
                                ticketList = ticketRepository.findTicketsByDepartmentAndStatus(user.getDepartment(), statusRepository.findStatusByStatus("NEW"));
                            } else if (user.getRoleSet().contains(roleRepository.findRoleByName("USER"))) {
                                ticketList = ticketRepository.findTicketsByNotifierAndStatus(user,statusRepository.findStatusByStatus("NEW"));
                            } else {
                                ticketList = ticketRepository.findAllByStatus("NEW");
                            }
                        } else {
                            if (sortDirection.equals("ASC")) {
                                if (user.getRoleSet().contains(roleRepository.findRoleByName("WORKER"))) {
                                    ticketList = ticketRepository.findTicketsByStatusAndUserListContaining(user.getEmail(), "NEW", Sort.by(Sort.Direction.ASC, "priority"));
                                } else if (user.getRoleSet().contains(roleRepository.findRoleByName("DEPARTMENT_BOSS"))) {
                                    ticketList = ticketRepository.findTicketsByDepartmentAndStatus(user.getDepartment(), statusRepository.findStatusByStatus("NEW"));
                                }  else if (user.getRoleSet().contains(roleRepository.findRoleByName("USER"))) {
                                    ticketList = ticketRepository.findTicketsByNotifierAndStatus(user,statusRepository.findStatusByStatus("NEW"));
                                }
                                else {
                                    ticketList = ticketRepository.findAllByStatus("NEW", Sort.by(Sort.Direction.ASC, "priority"));
                                }
                            } else if (sortDirection.equals("DESC")) {
                                if (user.getRoleSet().contains(roleRepository.findRoleByName("WORKER"))) {
                                    ticketList = ticketRepository.findTicketsByStatusAndUserListContaining(user.getEmail(), "NEW", Sort.by(Sort.Direction.DESC, "priority"));
                                } else if (user.getRoleSet().contains(roleRepository.findRoleByName("DEPARTMENT_BOSS"))) {
                                    ticketList = ticketRepository.findTicketsByDepartmentAndStatus(user.getDepartment(), statusRepository.findStatusByStatus("NEW"));
                                }  else if (user.getRoleSet().contains(roleRepository.findRoleByName("USER"))) {
                                    ticketList = ticketRepository.findTicketsByNotifierAndStatus(user,statusRepository.findStatusByStatus("NEW"));
                                }
                                else {
                                    ticketList = ticketRepository.findAllByStatus("NEW", Sort.by(Sort.Direction.DESC, "priority"));
                                }
                            }
                        }
                    }
                    case "IN PROGRESS" -> {
                        if (sortDirection == null || sortDirection.isEmpty()) {
                            if (user.getRoleSet().contains(roleRepository.findRoleByName("WORKER"))) {
                                ticketList = ticketRepository.findTicketsByStatusAndUserListContaining(user.getEmail(), "IN PROGRESS", Sort.by(Sort.Direction.ASC, "priority"));
                            } else if (user.getRoleSet().contains(roleRepository.findRoleByName("DEPARTMENT_BOSS"))) {
                                ticketList = ticketRepository.findTicketsByDepartmentAndStatus(user.getDepartment(), statusRepository.findStatusByStatus("IN PROGRESS"));
                            }  else if (user.getRoleSet().contains(roleRepository.findRoleByName("USER"))) {
                                ticketList = ticketRepository.findTicketsByNotifierAndStatus(user,statusRepository.findStatusByStatus("IN PROGRESS"));
                            }
                            else {
                                ticketList = ticketRepository.findAllByStatus("IN PROGRESS");
                            }
                        } else {
                            if (sortDirection.equals("ASC")) {
                                if (user.getRoleSet().contains(roleRepository.findRoleByName("WORKER"))) {
                                    ticketList = ticketRepository.findTicketsByStatusAndUserListContaining(user.getEmail(), "IN PROGRESS", Sort.by(Sort.Direction.ASC, "priority"));
                                } else if (user.getRoleSet().contains(roleRepository.findRoleByName("DEPARTMENT_BOSS"))) {
                                    ticketList = ticketRepository.findTicketsByDepartmentAndStatus(user.getDepartment(), statusRepository.findStatusByStatus("IN PROGRESS"));
                                }  else if (user.getRoleSet().contains(roleRepository.findRoleByName("USER"))) {
                                    ticketList = ticketRepository.findTicketsByNotifierAndStatus(user,statusRepository.findStatusByStatus("IN PROGRESS"));
                                } else {
                                    ticketList = ticketRepository.findAllByStatus("IN PROGRESS", Sort.by(Sort.Direction.ASC, "priority"));
                                }
                            } else if (sortDirection.equals("DESC")) {
                                if (user.getRoleSet().contains(roleRepository.findRoleByName("WORKER"))) {
                                    ticketList = ticketRepository.findTicketsByStatusAndUserListContaining(user.getEmail(), "IN PROGRESS", Sort.by(Sort.Direction.DESC, "priority"));
                                } else if (user.getRoleSet().contains(roleRepository.findRoleByName("DEPARTMENT_BOSS"))) {
                                    ticketList = ticketRepository.findTicketsByDepartmentAndStatus(user.getDepartment(), statusRepository.findStatusByStatus("IN PROGRESS"));
                                }  else if (user.getRoleSet().contains(roleRepository.findRoleByName("USER"))) {
                                    ticketList = ticketRepository.findTicketsByNotifierAndStatus(user,statusRepository.findStatusByStatus("IN PROGRESS"));
                                } else {
                                    ticketList = ticketRepository.findAllByStatus("IN PROGRESS", Sort.by(Sort.Direction.DESC, "priority"));
                                }
                            }
                        }
                    }
                    case "VERIFICATION" -> {
                        if (sortDirection == null || sortDirection.isEmpty()) {
                            if (user.getRoleSet().contains(roleRepository.findRoleByName("WORKER"))) {
                                ticketList = ticketRepository.findTicketsByStatusAndUserListContaining(user.getEmail(), "VERIFICATION", Sort.by(Sort.Direction.ASC, "priority"));
                            } else if (user.getRoleSet().contains(roleRepository.findRoleByName("DEPARTMENT_BOSS"))) {
                                ticketList = ticketRepository.findTicketsByDepartmentAndStatus(user.getDepartment(), statusRepository.findStatusByStatus("VERIFICATION"));
                            }  else if (user.getRoleSet().contains(roleRepository.findRoleByName("USER"))) {
                                ticketList = ticketRepository.findTicketsByNotifierAndStatus(user,statusRepository.findStatusByStatus("VERIFICATION"));
                            }
                            else {
                                ticketList = ticketRepository.findAllByStatus("VERIFICATION");
                            }
                        } else {
                            if (sortDirection.equals("ASC")) {
                                if (user.getRoleSet().contains(roleRepository.findRoleByName("WORKER"))) {
                                    ticketList = ticketRepository.findTicketsByStatusAndUserListContaining(user.getEmail(), "VERIFICATION", Sort.by(Sort.Direction.ASC, "priority"));
                                } else if (user.getRoleSet().contains(roleRepository.findRoleByName("DEPARTMENT_BOSS"))) {
                                    ticketList = ticketRepository.findTicketsByDepartmentAndStatus(user.getDepartment(), statusRepository.findStatusByStatus("VERIFICATION"));
                                } else if (user.getRoleSet().contains(roleRepository.findRoleByName("USER"))) {
                                    ticketList = ticketRepository.findTicketsByNotifierAndStatus(user,statusRepository.findStatusByStatus("VERIFICATION"));
                                }
                                else {
                                    ticketList = ticketRepository.findAllByStatus("VERIFICATION", Sort.by(Sort.Direction.ASC, "priority"));
                                }
                            } else if (sortDirection.equals("DESC")) {
                                if (user.getRoleSet().contains(roleRepository.findRoleByName("WORKER"))) {
                                    ticketList = ticketRepository.findTicketsByStatusAndUserListContaining(user.getEmail(), "VERIFICATION", Sort.by(Sort.Direction.DESC, "priority"));
                                } else if (user.getRoleSet().contains(roleRepository.findRoleByName("DEPARTMENT_BOSS"))) {
                                    ticketList = ticketRepository.findTicketsByDepartmentAndStatus(user.getDepartment(), statusRepository.findStatusByStatus("VERIFICATION"));
                                }  else if (user.getRoleSet().contains(roleRepository.findRoleByName("USER"))) {
                                    ticketList = ticketRepository.findTicketsByNotifierAndStatus(user,statusRepository.findStatusByStatus("VERIFICATION"));
                                }
                                else {
                                    ticketList = ticketRepository.findAllByStatus("VERIFICATION", Sort.by(Sort.Direction.DESC, "priority"));
                                }
                            }
                        }
                    }
                    case "DONE" -> {
                        if (sortDirection == null || sortDirection.isEmpty()) {
                            if (user.getRoleSet().contains(roleRepository.findRoleByName("DEPARTMENT_BOSS"))) {
                                ticketList = ticketRepository.findTicketsByDepartmentAndStatus(user.getDepartment(), statusRepository.findStatusByStatus("DONE"));
                            } else if (user.getRoleSet().contains(roleRepository.findRoleByName("USER"))) {
                                ticketList = ticketRepository.findTicketsByNotifierAndStatus(user,statusRepository.findStatusByStatus("DONE"));
                            }
                            else ticketList = ticketRepository.findAllByStatus("DONE");
                        } else {
                            if (sortDirection.equals("ASC")) {
                                if (user.getRoleSet().contains(roleRepository.findRoleByName("DEPARTMENT_BOSS"))) {
                                    ticketList = ticketRepository.findTicketsByDepartmentAndStatus(user.getDepartment(), statusRepository.findStatusByStatus("DONE"));
                                } else if (user.getRoleSet().contains(roleRepository.findRoleByName("USER"))) {
                                    ticketList = ticketRepository.findTicketsByNotifierAndStatus(user,statusRepository.findStatusByStatus("DONE"));
                                }
                                else
                                    ticketList = ticketRepository.findAllByStatus("DONE", Sort.by(Sort.Direction.ASC));
                            } else if (sortDirection.equals("DESC")) {
                                if (user.getRoleSet().contains(roleRepository.findRoleByName("DEPARTMENT_BOSS"))) {
                                    ticketList = ticketRepository.findTicketsByDepartmentAndStatus(user.getDepartment(), statusRepository.findStatusByStatus("DONE"));
                                }  else if (user.getRoleSet().contains(roleRepository.findRoleByName("USER"))) {
                                    ticketList = ticketRepository.findTicketsByNotifierAndStatus(user,statusRepository.findStatusByStatus("DONE"));
                                }
                                else
                                    ticketList = ticketRepository.findAllByStatus("DONE", Sort.by(Sort.Direction.DESC));
                            }
                        }
                    }
                    case "ARCHIVED" -> {
                        if (sortDirection == null || sortDirection.isEmpty()) {
                            if (user.getRoleSet().contains(roleRepository.findRoleByName("DEPARTMENT_BOSS"))) {
                                ticketList = ticketRepository.findTicketsByDepartmentAndStatus(user.getDepartment(), statusRepository.findStatusByStatus("ARCHIVED"));
                            } else if (user.getRoleSet().contains(roleRepository.findRoleByName("USER"))) {
                                ticketList = ticketRepository.findTicketsByNotifierAndStatus(user,statusRepository.findStatusByStatus("ARCHIVED"));
                            } else ticketList = ticketRepository.findAllByStatus("ARCHIVED");
                        } else {
                            if (sortDirection.equals("ASC")) {
                                if (user.getRoleSet().contains(roleRepository.findRoleByName("DEPARTMENT_BOSS"))) {
                                    ticketList = ticketRepository.findTicketsByDepartmentAndStatus(user.getDepartment(), statusRepository.findStatusByStatus("ARCHIVED"));
                                } else if (user.getRoleSet().contains(roleRepository.findRoleByName("USER"))) {
                                    ticketList = ticketRepository.findTicketsByNotifierAndStatus(user,statusRepository.findStatusByStatus("ARCHIVED"));
                                }
                                else
                                    ticketList = ticketRepository.findAllByStatus("ARCHIVED", Sort.by(Sort.Direction.ASC));
                            } else if (sortDirection.equals("DESC")) {
                                if (user.getRoleSet().contains(roleRepository.findRoleByName("DEPARTMENT_BOSS"))) {
                                    ticketList = ticketRepository.findTicketsByDepartmentAndStatus(user.getDepartment(), statusRepository.findStatusByStatus("ARCHIVED"));
                                } else if (user.getRoleSet().contains(roleRepository.findRoleByName("USER"))) {
                                    ticketList = ticketRepository.findTicketsByNotifierAndStatus(user,statusRepository.findStatusByStatus("ARCHIVED"));
                                } else
                                    ticketList = ticketRepository.findAllByStatus("ARCHIVED", Sort.by(Sort.Direction.DESC));
                            }
                        }
                    }
                    default -> {
                        if (user.getRoleSet().contains(roleRepository.findRoleByName("WORKER"))) {
                            ticketList = ticketRepository.findTicketsByStatusAndUserListContaining(user.getEmail());
                        } else if ((user.getRoleSet().contains(roleRepository.findRoleByName("DEPARTMENT_BOSS")))) {
                            ticketList = ticketRepository.findTicketsByDepartmentAnAndStatusNotContainingArchive(user.getDepartment());
                        }  else if (user.getRoleSet().contains(roleRepository.findRoleByName("USER"))) {
                            ticketList = ticketRepository.findTicketsByNotifier(user);
                        } else {
                            ticketList = ticketRepository.findAll();
                        }
                    }
                }
            }
            case "date" -> {
                if (sortDirection != null || !sortDirection.isEmpty()) {
                    if (sortDirection.equals("ASC"))
                        ticketList = ticketRepository.findAllByTicket_time(Sort.by(Sort.Direction.ASC));
                    else if (sortDirection.equals("DESC"))
                        ticketList = ticketRepository.findAllByTicket_time(Sort.by(Sort.Direction.DESC));
                }
            }
        }
        return ticketList;
    }



}
