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
    public final JavaMailSender javaMailSender;

    public NotifyService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String email, String title, String content)   throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("helpdesk@gmail.com", "HelpDesk App");
        messageHelper.setTo(email);
        messageHelper.setSubject(title);
        messageHelper.setText(content, true);
        javaMailSender.send(message);
    }
    public void sendEmail(String email,String[] cc, String title, String content)  throws MessagingException, UnsupportedEncodingException{
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("helpdesk@gmail.com", "HelpDesk App");
        messageHelper.setTo(email);
        messageHelper.setCc(cc);
        messageHelper.setSubject(title);
        messageHelper.setText(content, true);
        javaMailSender.send(message);
    }
}
