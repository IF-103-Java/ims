package com.ita.if103java.ims.controller;


import com.ita.if103java.ims.dto.UserLoginDto;
import com.ita.if103java.ims.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@CrossOrigin("http://localhost:4200")
public class LoginController {

    private LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/signin")
    public String singIn(@RequestBody UserLoginDto userLoginDto) {
        return loginService.signIn(userLoginDto);
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    public void forgotPassword(@RequestBody String email) {
        loginService.sendResetPasswordToken(email);
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.OK)
    public void resetPassword(@RequestParam("emailUUID") String emailUUID,
                              @RequestBody String newPassword) {
        loginService.resetPassword(emailUUID, newPassword);
    }

}
