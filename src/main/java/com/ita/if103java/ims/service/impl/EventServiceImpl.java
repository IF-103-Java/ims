package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.EventDao;
import com.ita.if103java.ims.dto.EventDto;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.mapper.EventDtoMapper;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Service
public class EventServiceImpl implements EventService {

    private EventDao eventDao;
    private EventDtoMapper eventDtoMapper;
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public EventServiceImpl(EventDao eventDao, EventDtoMapper eventDtoMapper,
                            SimpMessagingTemplate simpMessagingTemplate) {
        this.eventDao = eventDao;
        this.eventDtoMapper = eventDtoMapper;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    @Async("threadPoolTaskExecutor")
    public void create(Event event) {
        ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.systemDefault());
        event.setDate(currentDateTime);
        event = eventDao.create(event);
        if (event.getName().isNotification()) {
            simpMessagingTemplate.convertAndSend("/topic/event", eventDtoMapper.toDto(event));
        }
    }

    @Override
    public List<EventDto> findAll(Pageable pageable, Map<String, ?> params, UserDetailsImpl user) {
        return eventDtoMapper.toDtoList(eventDao.findAll(pageable, params, user.getUser()));
    }
}
