package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.UserLoginDto;

public interface LoginService {

    String signIn(UserLoginDto userLoginDto);

    void sendResetPasswordToken(String email);

    void resetPassword(String emailUUID, String newPassword);
}
