package com.perfect.hepdeskapp.status;

import com.perfect.hepdeskapp.config.Utility;
import com.perfect.hepdeskapp.department.Department;
import com.perfect.hepdeskapp.department.DepartmentRepository;
import com.perfect.hepdeskapp.notification.NotifyService;
import com.perfect.hepdeskapp.role.RoleRepository;
import com.perfect.hepdeskapp.ticket.Ticket;
import com.perfect.hepdeskapp.ticket.TicketRepository;
import com.perfect.hepdeskapp.user.User;
import com.perfect.hepdeskapp.user.UserRepository;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class StatusController {
    final
    StatusRepository statusRepository;
    final
    TicketRepository ticketRepository;
    final
    DepartmentRepository departmentRepository;
    final
    UserRepository userRepository;
    final
    RoleRepository roleRepository;
    final
    NotifyService notify;
    private final Environment environment;
    public StatusController(StatusRepository statusRepository, TicketRepository ticketRepository, NotifyService notify, Environment environment, DepartmentRepository departmentRepository, UserRepository userRepository, RoleRepository roleRepository) {
        this.statusRepository = statusRepository;
        this.ticketRepository = ticketRepository;
        this.notify = notify;
        this.environment = environment;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
    @GetMapping("/manager/api/getAllStatuses")
    @ResponseBody public List<Status> getAllStatuses(){
        return statusRepository.findAll();
    }

    @GetMapping("/worker/api/getAllStatuses")
    @ResponseBody public List<Status> getAllStatusesForWorkers(){
        return statusRepository.findStatusesByWorkerClass();
    }
    @PatchMapping("/worker/api/changeTicketTStatus")
    @ResponseBody public String changeTicketStatusWorker(HttpServletRequest request,
                                                   @RequestParam("ticket-id") Long ticket_id,
                                                   @RequestParam("status-id") Long status_id) throws IOException {
        Ticket ticket = ticketRepository.findTicketById(ticket_id);
        ticket.setStatus(statusRepository.findStatusById(status_id));
        ticketRepository.saveAndFlush(ticket);
        if(!Objects.requireNonNull(environment.getProperty("smtp.status")).equals("OFF")) {
            String url = Utility.getSiteURL(request) + "/status?ticket-id="+ ticket.getId() +"&ticket-token="+ticket.getAccess_token();
            try {

                List<String> tempEmails = new ArrayList<>();
                Department department = departmentRepository.findDepartmentById(ticket.getDepartment().getId());
                List<User> departmentBosses = userRepository.findUserByDepartmentAndRole(department,"DEPARTMENT_BOSS");
                for (User user : departmentBosses){
                    tempEmails.add(user.getEmail());
                }
                String[] emailsToCC = tempEmails.toArray(new String[0]);
                notify.sendEmail(ticket.getNotifier_email(), "HelpDesk | Your ticket changed status to "+ticket.getStatus().getStatus(), "<p>Welcome,</p>"
                        + "<p>we have changed the status of your ticket.</p>"
                        + "<p>Current status: " + ticket.getStatus().getStatus()+ "</p>"
                        + "<p>If you want to check the details of your ticket go to this link.</p>"
                        + "<p><a href=\"" + url + "\">Go to ticket</a></p>"
                        + "<br>"
                        + "<p>Thank you for your trust. "
                        + "Helpdesk system.</p>");
                if (ticket.getStatus().getStatus().equals("VERIFICATION")){
                            notify.sendEmail(emailsToCC[0],emailsToCC,"Helpdesk | Ticket is waiting for verification!","<p>Welcome,</p>" +
                            "<p>the worker has ended his work, and marked the ticket as <strong>ready to verification</strong></p>" +
                            "<p>To see the ticket click link bellow</p>" +
                            "<p><a href=\"" + url + "\">Go to ticket</a></p>" +
                            "<p>Thanks for your trust.</p>" +
                            "<p>HelpDesk System.</p>");
                }
            } catch (MessagingException e) {
                return "ERROR";
            }
        }
        return ticket.getStatus().getStatus();
    }
    @PatchMapping("/manager/api/changeTicketStatus")
    @PreAuthorize("hasAnyRole('DEPARTMENT_BOSS','ADMIN')")
    @ResponseBody public String changeTicketStatusManager(HttpServletRequest request,
                                                          @RequestParam("ticket-id") Long ticket_id,
                                                          @RequestParam("status-id") Long status_id) throws IOException{
        Ticket ticket = ticketRepository.findTicketById(ticket_id);
        ticket.setStatus(statusRepository.findStatusById(status_id));

        ticketRepository.saveAndFlush(ticket);
        if(!Objects.requireNonNull(environment.getProperty("smtp.status")).equals("OFF")) {

            String url = Utility.getSiteURL(request) + "/status?ticket-id="+ ticket.getId() +"&ticket-token="+ticket.getAccess_token();
            try {
                notify.sendEmail(ticket.getNotifier_email(), "HelpDesk | Your ticket changed status to "+ticket.getStatus().getStatus(), "<p>Welcome,</p>"
                        + "<p>we have changed the status of your ticket.</p>"
                        + "<p>Current status: " + ticket.getStatus().getStatus()+ "</p>"
                        + "<p>If you want to check the details of your ticket go to this link.</p>"
                        + "<p><a href=\"" + url + "\">Go to ticket</a></p>"
                        + "<br>"
                        + "<p>Thank you for your trust. "
                        + "Helpdesk system.</p>");
            } catch (MessagingException e) {
                return "SMTP ERROR";
            }
        }
        return ticket.getStatus().getStatus();
    }
}
