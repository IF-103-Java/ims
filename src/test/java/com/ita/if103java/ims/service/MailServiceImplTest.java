package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.mapper.dto.UserDtoMapper;
import com.ita.if103java.ims.service.impl.MailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static com.ita.if103java.ims.DataUtil.getTestUser;
import static com.ita.if103java.ims.config.MailMessagesConfig.FOOTER;
import static com.ita.if103java.ims.config.MailMessagesConfig.RESET_PASSWORD;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MailServiceImplTest {
    @Value("${mail.resetPasswordURL}")
    private String resetPasswordURL;

    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private MimeMessage mimeMessage;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MailServiceImpl mailService;

    private UserDtoMapper mapper;
    private String message;
    private String subject;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mapper = new UserDtoMapper();
        mailService = new MailServiceImpl(javaMailSender);
        when(this.passwordEncoder.encode(anyString())).thenReturn("$2a$10$NXNtx47QhcfV1Uxf5lGYK.JJcIqbjgaQpSHlVfCX31HsvglFzLgi6");
        userDto = mapper.toDto(getTestUser());
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        message = "" +
            RESET_PASSWORD +
            resetPasswordURL + userDto.getEmailUUID() + "\n" +
            FOOTER;
        subject = "ACTIVATION";
    }

    @Test
    void sendMessage_successFlow() throws MessagingException {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        mailService.sendMessage(userDto, message, subject);
        //Checking if method was called
        verify(javaMailSender).send((mimeMessage));
    }
}
