package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.User;

public interface InvitationService {
    void inviteUser(User accountAdmin, UserDto userDto);
}
