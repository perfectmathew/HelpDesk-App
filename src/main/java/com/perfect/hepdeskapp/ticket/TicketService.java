package com.perfect.hepdeskapp.ticket;

import com.perfect.hepdeskapp.notification.NotifyService;
import com.perfect.hepdeskapp.priority.Priority;
import com.perfect.hepdeskapp.priority.PriorityRepository;
import com.perfect.hepdeskapp.status.Status;
import com.perfect.hepdeskapp.status.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class TicketService {
    final
    TicketRepository ticketRepository;
    final
    PriorityRepository priorityRepository;
    final
    StatusRepository statusRepository;
    @Autowired
    NotifyService notify;
    private final Environment environment;


    public TicketService(TicketRepository ticketRepository, PriorityRepository priorityRepository, StatusRepository statusRepository, Environment environment) {
        this.ticketRepository = ticketRepository;
        this.priorityRepository = priorityRepository;
        this.statusRepository = statusRepository;
        this.environment = environment;
    }

    public boolean updateTicketPriority(Ticket ticket,Priority priority){
        ticket.setPriority(priority);
        ticketRepository.saveAndFlush(ticket);
        return true;
    }
    public boolean updateTicketStatus(Ticket ticket, Status status){
        ticket.setStatus(status);
        ticketRepository.saveAndFlush(ticket);
        return true;
    }
    public String generateHash(){
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijk"
                +"lmnopqrstuvwxyz@";
        Random rnd = new Random();
        StringBuilder hash = new StringBuilder(10);
        for (int i = 0; i < 10; i++) hash.append(chars.charAt(rnd.nextInt(chars.length())));

        return hash.toString();
    }
}
