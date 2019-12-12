package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.dto.transfer.NewData;
import com.ita.if103java.ims.service.MailService;
import com.ita.if103java.ims.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.ita.if103java.ims.config.MailMessagesConfig.ACTIVATE_USER;
import static com.ita.if103java.ims.config.MailMessagesConfig.FOOTER;

@RestController
@RequestMapping("/registration")

public class RegistrationController {

    @Value("${mail.activationURL}")
    private String activationURL;

    private UserService userService;
    private MailService mailService;

    @Autowired
    public RegistrationController(UserService userService, MailService mailService) {
        this.userService = userService;
        this.mailService = mailService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@Validated({NewData.class}) @RequestBody UserDto userDto) {
        UserDto createdUser = userService.create(userDto);
        String token = createdUser.getEmailUUID();
        String message = "" +
            ACTIVATE_USER
            + activationURL + token + "\n" +
            FOOTER;
        mailService.sendMessage(createdUser, message, "ACCOUNT ACTIVATION");

    }

}
