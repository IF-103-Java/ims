package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.mapper.UserDtoMapper;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/invite")
public class InvitationController {
    private InvitationService invitationService;
    private UserDtoMapper userDtoMapper;

    @Autowired
    public InvitationController(InvitationService invitationService, UserDtoMapper userDtoMapper) {
        this.invitationService = invitationService;
        this.userDtoMapper = userDtoMapper;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/")
    public void invite(@AuthenticationPrincipal UserDetailsImpl user, @RequestBody UserDto userDto) {
        invitationService.inviteUser(userDtoMapper.toDto(user.getUser()), userDto);
    }
}
