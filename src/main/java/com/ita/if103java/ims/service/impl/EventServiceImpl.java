package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.EventDao;
import com.ita.if103java.ims.dto.EventDto;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.mapper.EventDtoMapper;
import com.ita.if103java.ims.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Service
@PropertySource("classpath:application.properties")
public class EventServiceImpl implements EventService {

    private EventDao eventDao;
    private EventDtoMapper eventDtoMapper;

    @Autowired
    public EventServiceImpl(EventDao eventDao, EventDtoMapper eventDtoMapper) {
        this.eventDao = eventDao;
        this.eventDtoMapper = eventDtoMapper;
    }

    @Override
    public EventDto create(EventDto eventDto) {
        Event event = eventDao.create(eventDtoMapper.convertEventDtoToEvent(eventDto));
        ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.systemDefault());
        event.setDate(currentDateTime);
        return eventDtoMapper.convertEventToEventDto(event);
    }

    @Override
    public EventDto findById(Long id) {
        return eventDtoMapper.convertEventToEventDto(eventDao.findById(id));
    }

    @Override
    public List<EventDto> findAll(Map<String, ?> params) {
        return eventDtoMapper.convertToEventDtoList(eventDao.findAll(params));
    }
}
