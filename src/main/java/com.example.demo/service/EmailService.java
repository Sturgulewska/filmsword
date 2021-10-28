package com.example.demo.service;

import com.example.demo.configuration.EmailConfiguration;
import com.sun.mail.util.MailSSLSocketFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.Properties;

@Service

public class EmailService {
    private final JavaMailSender javaMailSender;

    private final EmailConfiguration emailConfiguration;

    public EmailService(EmailConfiguration emailConfiguration) throws GeneralSecurityException {
        JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(emailConfiguration.getHost());
        mailSenderImpl.setPassword(emailConfiguration.getPassword());
        mailSenderImpl.setPort(emailConfiguration.getPort());
        mailSenderImpl.setUsername(emailConfiguration.getUsername());

        MailSSLSocketFactory socketFactory = new MailSSLSocketFactory();
        socketFactory.setTrustAllHosts(true);
        
        Properties props = mailSenderImpl.getJavaMailProperties();
        props.put("mail.smtp.auth", emailConfiguration.getEmailAuth());
        props.put("mail.smtp.starttls.enable", emailConfiguration.getEmailStartTls());
        props.put("mail.smtp.ssl.socketFactory", socketFactory);

        this.javaMailSender = mailSenderImpl;
        this.emailConfiguration = emailConfiguration;
    }

    public void sendEmail(String email, String tittle, String body, Map<String, File> attachments) throws MessagingException {
        MimeMessage message = createMessage(email, tittle, body, attachments);
        javaMailSender.send(message);
    }

    public void sendEmail(String email, String tittle, String body) throws MessagingException {
        MimeMessage message = createMessage(email, tittle, body, null);
        javaMailSender.send(message);
    }

    private MimeMessage createMessage(String email, String title, String body, Map<String, File> attachments) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
        messageHelper.setSubject(title);
        messageHelper.setText(body);
        messageHelper.setTo(email);

        if (attachments != null) {
            for (Map.Entry<String, File> entry : attachments.entrySet()) {
                String filename = entry.getKey();
                File file = entry.getValue();
                messageHelper.addAttachment(filename, file);
            }
        }

        messageHelper.setFrom(emailConfiguration.getEmailSender());
        return message;
    }
}
