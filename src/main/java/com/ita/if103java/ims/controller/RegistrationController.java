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
import org.springframework.web.bind.annotation.*;

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
        sendActivationMessage(createdUser, activationURL);
    }

    private void sendActivationMessage(UserDto userDto, String activationURL) {
        String message = "" +
            "Hello, we are happy to see you in our Inventory Management System.\n" +
            "Please, follow link bellow to activate your account:\n";
        mailService.sendMessage(userDto, message + activationURL, "Account activation");
    }
}
