package com.perfect.hepdeskapp;

import com.perfect.hepdeskapp.attachment.AttachmentRepository;
import com.perfect.hepdeskapp.documentation.DocumentationRepository;
import com.perfect.hepdeskapp.ticket.Ticket;
import com.perfect.hepdeskapp.ticket.TicketRepository;
import com.perfect.hepdeskapp.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class HepDeskAppApplicationTests {
	@Autowired
	AttachmentRepository attachmentRepository;
	@Autowired
	DocumentationRepository documentationRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	TicketRepository ticketRepository;
	@Test
	void contextLoads() {
	}

	@Test
	List<Ticket> getTicketByStatus(){
		List<Ticket> ticketList = ticketRepository.findAll();
		return ticketList;
	}

}
