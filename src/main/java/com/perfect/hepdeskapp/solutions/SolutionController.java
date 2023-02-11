package com.perfect.hepdeskapp.solutions;

import com.perfect.hepdeskapp.config.CustomErrorException;
import com.perfect.hepdeskapp.config.Utility;
import com.perfect.hepdeskapp.notification.NotifyService;
import com.perfect.hepdeskapp.task.TaskRepository;
import com.perfect.hepdeskapp.ticket.Ticket;
import com.perfect.hepdeskapp.ticket.TicketRepository;
import com.perfect.hepdeskapp.user.User;
import com.perfect.hepdeskapp.user.UserRepository;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Controller
public class SolutionController {
    private final TicketRepository ticketRepository;
    private final SolutionRepository solutionRepository;
    private final TaskRepository taskRepository;
    final
    Environment environment;
    private final NotifyService notify;
    private final UserRepository userRepository;

    public SolutionController(TicketRepository ticketRepository,
                              SolutionRepository solutionRepository,
                              TaskRepository taskRepository, Environment environment, NotifyService notify, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.solutionRepository = solutionRepository;
        this.taskRepository = taskRepository;
        this.environment = environment;
        this.notify = notify;
        this.userRepository = userRepository;
    }

    @PatchMapping("/user/api/t/{ticket_id}/solution/{action}")
    @ResponseBody
    public boolean solutionAction(@PathVariable Long ticket_id,@PathVariable boolean action){
        Ticket ticket = ticketRepository.findTicketById(ticket_id);
        if(ticket == null) throw new CustomErrorException("This ticket does not exists!");
        ticket.getSolution().setAccepted(action);
        ticketRepository.saveAndFlush(ticket);
        return action;
    }
    @PostMapping("/worker/api/solution/add")
    @ResponseBody
    public Solution createNewSolution(@RequestParam String solution_text, @RequestParam Long ticket_id){
        Solution solution = new Solution(solution_text,false);
        Ticket ticket = ticketRepository.findTicketById(ticket_id);
        if(ticket == null) throw new CustomErrorException("This ticket does not exists!");
        solutionRepository.saveAndFlush(solution);
        ticket.setSolution(solution);
        ticketRepository.saveAndFlush(ticket);
        return solution;
    }
    @PatchMapping("/worker/api/solution/edit")
    @ResponseBody
    public Solution editSolution(@RequestParam Long solution_id, @RequestParam String solution_text){
        Solution solution = solutionRepository.findSolutionById(solution_id);
        if(solution == null) throw new CustomErrorException("This solution does not exist.");
        solution.setSolution(solution_text);
        solutionRepository.saveAndFlush(solution);
        return solution;
    }
    @PatchMapping("/user/api/solution/reject")
    @ResponseBody
    public Solution rejectSolution(HttpServletRequest request, @RequestParam Long solution_id, @RequestParam String reject_solution_text, @RequestParam Long ticket_id) throws IOException {
        Solution solution = solutionRepository.findSolutionById(solution_id);
        Ticket ticket = ticketRepository.findTicketById(ticket_id);
        if(solution == null) throw new CustomErrorException("This solution does not exist.");
        if (ticket == null) throw new CustomErrorException("Ticket with this solution does not exists.");
        if(reject_solution_text.length() > 255) throw new CustomErrorException("Reject description must contain less letters than 256.");
            solution.setAccepted(false);
            solution.setRejection_description(reject_solution_text);
            List<String> tempEmails = new ArrayList<>();
            for (User user : ticket.getUserList()){
            tempEmails.add(user.getEmail());
            }
        String url = Utility.getSiteURL(request) + "/t/"+ ticket.getId();
        String[] emailsToCC = tempEmails.toArray(new String[0]);
        if (!Objects.requireNonNull(environment.getProperty("smtp.status")).equals("OFF")) {
            try {
                notify.sendEmail(emailsToCC[0],emailsToCC, "HelpDesk | The solution has been rejected!", "<p>Hello, user rejected the solution.</p>" +
                        "<p>Rejection reason: "+solution.getRejection_description()+"</p>" +
                        "<p>Please fix it and resend it.</p>" +
                        "<p>Ticket no. "+ticket.getId()+"</p>" +
                        "<p> Click on this <a href='" + url + "'> link</a> to go to the ticket page.</p>" +
                        "<p>Thanks,<br> Help Desk System</p>");
            } catch (MessagingException me) {
                me.printStackTrace();
                throw new CustomErrorException("User added successfully, but there was an error with the smtp server.");
            }
        }
        solutionRepository.saveAndFlush(solution);

        return solution;
    }
    @DeleteMapping("/worker/api/solution/delete")
    @ResponseBody
    public String deleteSolution(@RequestParam Long solution_id, @RequestParam Long ticket_id){
        Ticket ticket = ticketRepository.findTicketById(ticket_id);
        Solution solution =  solutionRepository.findSolutionById(solution_id);
        if(ticket == null) throw new CustomErrorException("This ticket does not exist.");
        if(solution == null) throw new CustomErrorException("This solution does not exist.");
        ticket.setSolution(null);
        ticketRepository.saveAndFlush(ticket);
        solutionRepository.delete(solution);
        return "Successful";
    }
    @GetMapping("/worker/api/solution/getAll")
    @ResponseBody
    public Page<Solution> getAllSolutions(@RequestParam int page, @RequestParam int size){
        return solutionRepository.findAll(PageRequest.of(page,size));
    }
    @GetMapping("/worker/api/solution/getAllByText")
    @ResponseBody
    public List<Solution> findSolutionByText(@RequestParam String text){
        if (text.equals("")) return solutionRepository.findAll();
        return solutionRepository.findSolutionsByText(text);
    }
    @GetMapping("/worker/api/solution/get")
    @ResponseBody
    public Solution getSolution(@RequestParam Long solution_id){
        return solutionRepository.findSolutionById(solution_id);
    }
}
