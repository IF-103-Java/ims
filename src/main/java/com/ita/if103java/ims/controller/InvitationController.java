package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.service.InvitationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invite")
public class InvitationController {
    private InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @PostMapping("/{id}")
    public void invite(@PathVariable("id") Long accountId, @RequestBody String email) {
        invitationService.inviteUser(email, accountId);
    }
}
