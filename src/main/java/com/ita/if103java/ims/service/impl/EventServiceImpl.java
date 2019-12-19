package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.EventDao;
import com.ita.if103java.ims.dto.EventDto;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.mapper.EventDtoMapper;
import com.ita.if103java.ims.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
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
    // Visible for testing
    static final int PAGINATION_PAGE_SIZE = 2;

    @Autowired
    public EventServiceImpl(EventDao eventDao, EventDtoMapper eventDtoMapper, SimpMessagingTemplate simpMessagingTemplate) {
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
        simpMessagingTemplate.convertAndSend("/topic/event.create", eventDtoMapper.toDto(event));
    }

    @Override
    public EventDto findById(Long id) {
        return eventDtoMapper.toDto(eventDao.findById(id));
    }

    @Override
    public List<EventDto> findAll(int pageId, Map<String, ?> params) {
        if (pageId < 1) {
            throw new IllegalArgumentException("Page id should be greater than 0");
        }
        int offset = (pageId - 1) * PAGINATION_PAGE_SIZE;
        int limit = PAGINATION_PAGE_SIZE;

        return eventDtoMapper.toDtoList(eventDao.findAll(limit, offset, params));
    }
}
