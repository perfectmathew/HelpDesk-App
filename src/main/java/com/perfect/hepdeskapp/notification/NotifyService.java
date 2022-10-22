package com.perfect.hepdeskapp.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class NotifyService {
    @Autowired
    public JavaMailSender javaMailSender;

    public void sendEmail(String email, String tytul, String kontent)   throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("helpdesk@gmail.com", "HelpDesk App");
        helper.setTo(email);
        String subject = tytul;
        String content = kontent;
        helper.setSubject(subject);
        helper.setText(content, true);
        javaMailSender.send(message);
    }
}
