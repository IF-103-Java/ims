package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.EventDto;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.EventType;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/events")
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

    @GetMapping("/types")
    public Map<String, EventType> getEventTypes() {
        return eventService.getEventTypes();
    }

    @GetMapping("/names")
    public Map<String, EventName> getEventNames() {
        return eventService.getEventNames();
    }

}
