package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/invite")
public class InvitationController {
    private InvitationService invitationService;

    @Autowired
    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @PostMapping("/")
    public void invite(@AuthenticationPrincipal User user, @RequestBody UserDto userDto) {
        invitationService.inviteUser(user, userDto);
    }
}
