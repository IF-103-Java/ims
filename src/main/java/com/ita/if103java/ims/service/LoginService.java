package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.UserLoginDto;
import org.springframework.http.ResponseEntity;

public interface LoginService {

    ResponseEntity signIn(UserLoginDto userLoginDto);

    void sendResetPasswordToken(String email);

    void resetPassword(String emailUUID, String newPassword);
}
