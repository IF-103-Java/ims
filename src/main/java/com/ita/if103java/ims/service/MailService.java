package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.UserDto;

public interface MailService {
    void sendMessage(UserDto userDto, String message, String subject);
}
