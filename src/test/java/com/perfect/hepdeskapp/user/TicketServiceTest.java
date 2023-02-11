package com.perfect.hepdeskapp.user;

import com.perfect.hepdeskapp.attachment.Attachment;
import com.perfect.hepdeskapp.department.DepartmentRepository;
import com.perfect.hepdeskapp.role.RoleRepository;
import com.perfect.hepdeskapp.ticket.Ticket;
import com.perfect.hepdeskapp.ticket.TicketService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TicketServiceTest {
    @Autowired
    TicketService ticketService;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSubmitTicket() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        // Szuka obiektu w bazie
        User user = userRepository.findUserByEmail("doe@john.com");
        // Tworzy plik załącznika
        MultipartFile[] files = new MultipartFile[1];
        files[0] = new MockMultipartFile("file", "attachment.txt", "text/plain",
                "Hello, this is an attachment".getBytes());
        // Wywołuje metodę sumbmitTicket() z seerwisu zgłoszeń
        Ticket ticket = ticketService.submitTicket(request, "This is my ticket content", "IT", files, user);
        // Sprawdza czy zgłoszenie zostało zapisane
        assertNotNull(ticket);
        assertEquals("This is my ticket content", ticket.getDescription());
        assertEquals("IT", ticket.getDepartment().getName());
        assertEquals(user, ticket.getNotifier());
        // Sprawdza czy załączniki  zostały zapisane
        List<Attachment> attachments = ticket.getAttachmentSet();
        assertNotNull(attachments);
        assertEquals(1, attachments.size());
        Attachment attachment = attachments.iterator().next();
        assertEquals("attachment.txt", attachment.getName());
        assertNotNull(attachment.getUrl());
    }

}
