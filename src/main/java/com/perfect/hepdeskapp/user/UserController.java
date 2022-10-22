package com.perfect.hepdeskapp.user;

import com.perfect.hepdeskapp.ticket.Ticket;
import com.perfect.hepdeskapp.ticket.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    TicketRepository ticketRepository;
    @RequestMapping(value = "/admin")
    public String adminPanel(Model model){
        List<Ticket> ticketList = ticketRepository.findAll();
        model.addAttribute("ticketList",ticketList);
        return "admin/admin_panel";

    }


}
