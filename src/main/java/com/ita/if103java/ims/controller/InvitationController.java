package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invite")
public class InvitationController {
    private InvitationService invitationService;

    @Autowired
    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @PostMapping("/")
    public void invite(@RequestBody UserDto userDto) {
        invitationService.inviteUser(userDto);
    }
}
