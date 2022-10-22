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
import com.perfect.hepdeskapp.user.UserRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Controller
public class TicketController {
    private final TicketRepository ticketRepository;
    private final DepartmentRepository departmentRepository;
    private final StatusRepository statusRepository;
    private final AttachmentRepository attachmentRepository;
    private final DocumentationRepository documentationRepository;
    final
    UserRepository userRepository;
    @Autowired
    NotifyService notify;

    public TicketController(TicketRepository ticketRepository, DepartmentRepository departmentRepository, StatusRepository statusRepository, AttachmentRepository attachmentRepository,UserRepository userRepository, DocumentationRepository documentationRepository) {
        this.ticketRepository = ticketRepository;
        this.departmentRepository = departmentRepository;
        this.statusRepository = statusRepository;
        this.attachmentRepository = attachmentRepository;
        this.userRepository = userRepository;
        this.documentationRepository = documentationRepository;
    }
    @PostMapping(value = "/sendTicket")
    @ResponseBody
    public String submitTicket(Model model, HttpServletRequest request, Ticket ticket, Department department, Attachment attachment,
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
        Date date = new Date();
        if(files != null){
            List<Attachment> attachmentsList = new ArrayList<>();
            Path root = Paths.get("src","main","webapp","WEB-INF", "uploads");
            String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijk"
                    +"lmnopqrstuvwxyz@";
            Random rnd = new Random();
            StringBuilder hash = new StringBuilder(10);
            for (int i = 0; i < 10; i++) hash.append(chars.charAt(rnd.nextInt(chars.length())));
            String uploadDir = root.toString() + "/" + hash.toString();
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
        String url = Utility.getSiteURL(request) + "/status?ticketid="+ String.valueOf(ticket.getId()) +"&tickettoken="+token;
        try {
            notify.sendEmail(email,"HelpDesk | Twoje zgłoszenie zostało wysłane!","<p>Witaj,</p>"
                    + "<p>przekazaliśmy twoje zgłoszenie do odpowiednich osób.</p>"
                    + "<p>Numer twojego zgłoszenia to: "+ticket.getId()+"</p>"
                    + "<p>Hasło dostępowe twojego zgłoszenia to: "+token+"</p>"
                    + "<p><a href=\"" + url + "\">przejdź do zgłoszenia</a></p>"
                    + "<br>"
                    + "<p>Dziękujemy za zaufanie. "
                    + "Zespół: helpdesk.com .</p>");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return "index";
    }
    
    @RequestMapping("/t/{ticketid}")
    public String ticketDetails(@PathVariable(value = "ticketid") long id,Model model){
        Ticket ticket = ticketRepository.findTicketById(id);
        Documentation documentation = documentationRepository.findDocumentationByTicket(ticket);
        model.addAttribute("ticket",ticket);
        model.addAttribute("documentation",documentation);
        return "ticket/ticket_view";
    }
}
