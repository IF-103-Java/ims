package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.EventDto;
import com.ita.if103java.ims.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public EventDto getEventById(@RequestParam long id) {
        return eventService.findById(id);
    }
}
