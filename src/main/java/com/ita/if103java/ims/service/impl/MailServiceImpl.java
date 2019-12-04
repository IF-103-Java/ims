package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.service.MailService;
import com.mchange.net.MailSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class MailServiceImpl implements MailService {

    private static final Logger LOGGER = LogManager.getLogger(MailSender.class);
    private final JavaMailSender javaMailSender;

    @Autowired
    public MailServiceImpl(@Qualifier("getMailSender") JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendMessage(UserDto userDto, String message, String subject) {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(userDto.getEmail()));
            mimeMessage.setSubject(subject);
            mimeMessage.setContent(message, "text/html");
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            String msg = "Sending message " + subject + " to user id( " + userDto.getId() + ") was failed!";
            LOGGER.error(msg, e);
        }
    }
}
