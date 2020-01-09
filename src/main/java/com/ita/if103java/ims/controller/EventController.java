package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.EventDto;
import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/events")
@CrossOrigin(value = "http://localhost:4200")
public class EventController {
    private EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public Page<EventDto> findAll(Pageable pageable, @RequestBody Map<String, ?> params, @AuthenticationPrincipal UserDetailsImpl user) {
        return eventService.findAll(pageable, params, user);
    }

    @PostMapping("/test")
    public Page<EventDto> findAllPost(Pageable pageable, @RequestBody Map<String, ?> params) {
        System.out.println("params:::" + params);
        User user1 = new User();
        user1.setRole(Role.ROLE_ADMIN);
        user1.setAccountId((long)2);
        UserDetailsImpl user = new UserDetailsImpl(user1, new AccountType());
        return eventService.findAll(pageable, params, user);
    }

    @GetMapping("/test")
    public Page<EventDto> findAllGet(Pageable pageable) {
        User user1 = new User();
        user1.setRole(Role.ROLE_ADMIN);
        user1.setAccountId((long)2);
        UserDetailsImpl user = new UserDetailsImpl(user1, new AccountType());
        eventService.create(new Event("hello", (long)2, null, (long) 25, EventName.SIGN_UP, null));
        return eventService.findAll(pageable, new HashMap<>(), user);
    }

}
