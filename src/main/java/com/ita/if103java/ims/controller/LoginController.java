package com.ita.if103java.ims.controller;


import com.ita.if103java.ims.dto.ForgotPasswordDto;
import com.ita.if103java.ims.dto.UserLoginDto;
import com.ita.if103java.ims.dto.transfer.NewData;
import com.ita.if103java.ims.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class LoginController {

    private LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping(value = "/signin", produces = "application/json")
    public ResponseEntity singIn(@RequestBody UserLoginDto userLoginDto) {
        return loginService.signIn(userLoginDto);
    }

    @PostMapping(value = "/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    public void forgotPassword(@Validated({NewData.class}) @RequestBody ForgotPasswordDto forgotPasswordDto) {
        loginService.sendResetPasswordToken(forgotPasswordDto);
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.OK)
    public void resetPassword(@RequestParam("emailUUID") String emailUUID,
                              @RequestBody String newPassword) {
        loginService.resetPassword(emailUUID, newPassword);
    }

}
