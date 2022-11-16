package com.perfect.hepdeskapp.ticket;

import com.perfect.hepdeskapp.attachment.Attachment;
import com.perfect.hepdeskapp.attachment.AttachmentRepository;
import com.perfect.hepdeskapp.config.Utility;
import com.perfect.hepdeskapp.department.Department;
import com.perfect.hepdeskapp.department.DepartmentRepository;
import com.perfect.hepdeskapp.documentation.Documentation;
import com.perfect.hepdeskapp.documentation.DocumentationRepository;
import com.perfect.hepdeskapp.notification.NotifyService;
import com.perfect.hepdeskapp.status.Status;
import com.perfect.hepdeskapp.status.StatusRepository;
import com.perfect.hepdeskapp.config.FileUploadService;
import com.perfect.hepdeskapp.user.User;
import com.perfect.hepdeskapp.user.UserDetail;
import com.perfect.hepdeskapp.user.UserRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;

@Controller
public class TicketController {
    private final TicketRepository ticketRepository;
    private final DepartmentRepository departmentRepository;
    private final StatusRepository statusRepository;
    private final AttachmentRepository attachmentRepository;
    private final DocumentationRepository documentationRepository;
    private final Environment environment;
    final
    UserRepository userRepository;
    @Autowired
    NotifyService notify;

    public TicketController(TicketRepository ticketRepository, DepartmentRepository departmentRepository, StatusRepository statusRepository, AttachmentRepository attachmentRepository,UserRepository userRepository, DocumentationRepository documentationRepository, Environment environment) {
        this.ticketRepository = ticketRepository;
        this.departmentRepository = departmentRepository;
        this.statusRepository = statusRepository;
        this.attachmentRepository = attachmentRepository;
        this.userRepository = userRepository;
        this.documentationRepository = documentationRepository;
        this.environment = environment;
    }
    // Request to ticket list
    // FOR ROLE ADMIN
    @RequestMapping(value = "/admin/tickets")
    public String adminTicketList(Model model){
        model.addAttribute("statusList",statusRepository.findAll());
        model.addAttribute("ticketList",ticketRepository.findAllByTicket_time());
        return "admin/ticket_list";
    }
    // Request to ticket list
    // FOR ROLE DEPARTMENT_BOSS
    @RequestMapping(value = "/manager/tickets")
    public String managerTicketList(Model model){
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        String email = ((UserDetail)principal).getUsername();
        User user = userRepository.findUserByEmail(email);
        model.addAttribute("statusList",statusRepository.findAll());
        model.addAttribute("ticketList",ticketRepository.findTicketsByDepartment(user.getDepartment(),Sort.by(Sort.Direction.DESC,"id")));
        return "manager/ticket_list";
    }
    // Submit ticket function
    @PostMapping(value = "/sendTicket")
    @ResponseBody
    public String submitTicket(HttpServletRequest request, Ticket ticket, Department department,
                               @RequestParam("name") String name, @RequestParam("surname") String surname,
                               @RequestParam("email") String email, @RequestParam("phonenumber") String phone,
                               @RequestParam("description") String ticket_content,
                               @RequestParam("selectedDepartment") String selectedDepartment, @RequestParam(value = "attachments", required = false) MultipartFile[] files) throws IOException {

        switch (selectedDepartment){
            case "IT":
                department = departmentRepository.findDepartmentByName("IT");
                break;
            case "SERVICE":
                department = departmentRepository.findDepartmentByName("SERVICE");
                break;
            default:
                department = departmentRepository.findDepartmentByName("UNALLOCATED");
                break;
        }
        Status status = statusRepository.findStatusByStatus("NEW");
        String token = RandomString.make(30);
        Timestamp date = new Timestamp(System.currentTimeMillis());
        if(files != null){
            List<Attachment> attachmentsList = new ArrayList<>();
            Path root = Paths.get("src","main","webapp","WEB-INF", "uploads");
            String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijk"
                    +"lmnopqrstuvwxyz@";
            Random rnd = new Random();
            StringBuilder hash = new StringBuilder(10);
            for (int i = 0; i < 10; i++) hash.append(chars.charAt(rnd.nextInt(chars.length())));
            String uploadDir = root + "/" + hash;
            for(MultipartFile file : files){
                if(!file.isEmpty()){
                    FileUploadService.saveFile(uploadDir,file.getOriginalFilename(),file);
                    attachmentsList.add(new Attachment(file.getOriginalFilename(),"/uploads/"+hash+"/"+file.getOriginalFilename()));
                }
            }
            attachmentRepository.saveAllAndFlush(attachmentsList);
            ticket.getAttachmentSet().addAll(attachmentsList);
        }
        ticket.setNotifier_name(name);
        ticket.setNotifier_surname(surname);
        ticket.setDescription(ticket_content);
        ticket.setNotifier_email(email);
        ticket.setNotifier_phonenumber(phone);
        ticket.setStatus(status);
        ticket.setDepartment(department);
        ticket.setAccess_token(token);
        ticket.setTicket_time(date);
        ticketRepository.saveAndFlush(ticket);
        String url = Utility.getSiteURL(request) + "/status?ticket-id="+ ticket.getId() +"&ticket-token="+token;
        if(!environment.getProperty("smtp.status").equals("OFF")) {
            try {
                notify.sendEmail(email, "HelpDesk | Your ticket has been sent!", "<p>Welcome,</p>"
                        + "<p>we have forwarded your ticket to the appropriate people.</p>"
                        + "<p>Your ticket number is: " + ticket.getId() + "</p>"
                        + "<p>The access password for your ticket is: " + token + "</p>"
                        + "<p><a href=\"" + url + "\">Go to ticket</a></p>"
                        + "<br>"
                        + "<p>Thank you for your trust. "
                        + "Helpdesk system.</p>");
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return "index";
    }
    
    @RequestMapping("/t/{ticketid}")
    public String ticketDetails(@PathVariable(value = "ticketid") long id,Model model){
        Ticket ticket = ticketRepository.findTicketById(id);
        Documentation documentation = documentationRepository.findDocumentationByTicket(ticket);
        List<User> userList = userRepository.findUserByDepartmentAndRole(ticket.getDepartment(),"WORKER");
        model.addAttribute("ticket",ticket);
        model.addAttribute("documentation",documentation);
        model.addAttribute("workerList",userList);
        return "ticket/ticket_view";
    }
    @PostMapping("admin/ticket/delete")
    @ResponseBody
    public String deleteTicket(@RequestParam("ticket_id") Long ticket_id){
        Ticket ticket = ticketRepository.findTicketById(ticket_id);
        ticketRepository.delete(ticket);
        return "Successfully";
    }
    @PostMapping("/manager/api/assignWorker")
    @ResponseBody
    public String assignWorkerToTicket(@RequestParam("user_id") Long user_id, @RequestParam("ticket_id") Long ticket_id){
        User user = userRepository.findUserById(user_id);
        Ticket ticket = ticketRepository.findTicketById(ticket_id);
        ticket.addUserToTicket(user);
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
    @ResponseBody
    public String changeTicketDepartment(@RequestParam("department_id") Long department_id, @RequestParam("ticket_id") Long ticket_id){
        Ticket ticket = ticketRepository.findTicketById(ticket_id);
        Department department = departmentRepository.findDepartmentById(department_id);
        ticket.setDepartment(department);
        ticketRepository.saveAndFlush(ticket);
        return ticket.getDepartment().getName();
    }

    @GetMapping("/api/findTicketsBy")
    @ResponseBody
    public List<Ticket> findTicketBy(@RequestParam(value = "sortDirection", required = false) String sortDirection, @RequestParam("findBy") String findBy, @RequestParam("findValue") String findVal){
      List<Ticket> ticketList = null;
        switch (findBy){
            case "status":
                switch (findVal){
                    case "NEW":
                     ticketList = ticketRepository.findAllByStatus("NEW");
                            break;
                    case "IN PROGRESS":
                        if(sortDirection==null || sortDirection.isEmpty()) ticketList = ticketRepository.findAllByStatus("IN PROGRESS");
                        else {
                            if (sortDirection.equals("ASC")) ticketList = ticketRepository.findAllByStatus("IN PROGRESS", Sort.by(Sort.Direction.ASC));
                            else if (sortDirection.equals("DESC")) ticketList = ticketRepository.findAllByStatus("IN PROGRESS", Sort.by(Sort.Direction.DESC));
                        }
                        break;
                    case "CANCELED":
                        if(sortDirection==null || sortDirection.isEmpty()) ticketList = ticketRepository.findAllByStatus("CANCELED");
                        else {
                            if (sortDirection.equals("ASC")) ticketList = ticketRepository.findAllByStatus("CANCELED", Sort.by(Sort.Direction.ASC));
                            else if (sortDirection.equals("DESC")) ticketList = ticketRepository.findAllByStatus("CANCELED", Sort.by(Sort.Direction.DESC));
                        }
                        break;
                    case  "ARCHIVED":
                        if(sortDirection==null || sortDirection.isEmpty()) ticketList = ticketRepository.findAllByStatus("ARCHIVED");
                        else {
                            if (sortDirection.equals("ASC")) ticketList = ticketRepository.findAllByStatus("ARCHIVED", Sort.by(Sort.Direction.ASC));
                            else if (sortDirection.equals("DESC")) ticketList = ticketRepository.findAllByStatus("ARCHIVED", Sort.by(Sort.Direction.DESC));
                        }
                        break;
                    default:
                        ticketList = ticketRepository.findAll();
                        break;
                }
             break;
            case "date":
                    if(sortDirection != null || !sortDirection.isEmpty()){
                        if(sortDirection.equals("ASC")) ticketList = ticketRepository.findAllByTicket_time(Sort.by(Sort.Direction.ASC));
                        else if (sortDirection.equals("DESC")) ticketList = ticketRepository.findAllByTicket_time(Sort.by(Sort.Direction.DESC));
                    }
                break;
        }
        return ticketList;
    }


}
