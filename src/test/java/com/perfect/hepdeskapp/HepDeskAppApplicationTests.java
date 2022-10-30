package com.perfect.hepdeskapp;

import com.perfect.hepdeskapp.attachment.Attachment;
import com.perfect.hepdeskapp.attachment.AttachmentRepository;
import com.perfect.hepdeskapp.documentation.Documentation;
import com.perfect.hepdeskapp.documentation.DocumentationRepository;
import com.perfect.hepdeskapp.user.User;
import com.perfect.hepdeskapp.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

@SpringBootTest
class HepDeskAppApplicationTests {
	@Autowired
	AttachmentRepository attachmentRepository;
	@Autowired
	DocumentationRepository documentationRepository;
	@Autowired
	UserRepository userRepository;
	@Test
	void contextLoads() {
	}


}
