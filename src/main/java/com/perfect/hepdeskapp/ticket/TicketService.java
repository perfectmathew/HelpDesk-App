package com.perfect.hepdeskapp.ticket;

import com.perfect.hepdeskapp.attachment.Attachment;
import com.perfect.hepdeskapp.attachment.AttachmentRepository;
import com.perfect.hepdeskapp.config.FileUploadService;
import com.perfect.hepdeskapp.config.Utility;
import com.perfect.hepdeskapp.department.DepartmentRepository;
import com.perfect.hepdeskapp.notification.NotifyService;
import com.perfect.hepdeskapp.priority.PriorityRepository;
import com.perfect.hepdeskapp.status.StatusRepository;
import com.perfect.hepdeskapp.user.User;
import net.bytebuddy.utility.RandomString;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
public class TicketService {
    final
    TicketRepository ticketRepository;
    final
    PriorityRepository priorityRepository;
    final
    StatusRepository statusRepository;
    final
    NotifyService notify;
    private final Environment environment;
    final
    DepartmentRepository departmentRepository;
    final
    AttachmentRepository attachmentRepository;


    public TicketService(TicketRepository ticketRepository, PriorityRepository priorityRepository, StatusRepository statusRepository, NotifyService notify, Environment environment, DepartmentRepository departmentRepository, AttachmentRepository attachmentRepository) {
        this.ticketRepository = ticketRepository;
        this.priorityRepository = priorityRepository;
        this.statusRepository = statusRepository;
        this.notify = notify;
        this.environment = environment;
        this.departmentRepository = departmentRepository;
        this.attachmentRepository = attachmentRepository;
    }

    public String generateHash(){
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijk"
                +"lmnopqrstuvwxyz@";
        Random rnd = new Random();
        StringBuilder hash = new StringBuilder(10);
        for (int i = 0; i < 10; i++) hash.append(chars.charAt(rnd.nextInt(chars.length())));

        return hash.toString();
    }
    public Ticket submitTicket(HttpServletRequest request, String ticket_content, String selectedDepartment, MultipartFile[] files, User user) throws IOException {
        String token = RandomString.make(30);
        Timestamp date = new Timestamp(System.currentTimeMillis());
        Ticket ticket = new Ticket(ticket_content,token,date,priorityRepository.findPriorityByPriority_name("STANDARD"),
                statusRepository.findStatusByStatus("NEW"),departmentRepository.findDepartmentByName(selectedDepartment),user);
        if(files != null){
            List<Attachment> attachmentsList = new ArrayList<>();
            Path root = Paths.get("src","main","webapp","WEB-INF", "uploads");
            String hash = generateHash();
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
        ticketRepository.saveAndFlush(ticket);
        String url = Utility.getSiteURL(request) + "/status?ticket-id="+ ticket.getId() +"&ticket-token="+token;
        if(!Objects.requireNonNull(environment.getProperty("smtp.status")).equals("OFF")) {
            try {
                notify.sendEmail(user.getEmail(), "HelpDesk | Your ticket has been sent!", "<p>Welcome,</p>"
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
        return ticket;
    }
}
