package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.EventDao;
import com.ita.if103java.ims.dao.UserDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dto.EventDto;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.mapper.dto.EventDtoMapper;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private EventDao eventDao;
    private EventDtoMapper eventDtoMapper;
    private SimpMessagingTemplate simpMessagingTemplate;
    private UserDao userDao;
    private WarehouseDao warehouseDao;

    @Autowired
    public EventServiceImpl(EventDao eventDao, EventDtoMapper eventDtoMapper,
                            SimpMessagingTemplate simpMessagingTemplate, UserDao userDao,
                            WarehouseDao warehouseDao) {
        this.eventDao = eventDao;
        this.eventDtoMapper = eventDtoMapper;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userDao = userDao;
        this.warehouseDao = warehouseDao;
    }

    @Override
    @Async("threadPoolTaskExecutor")
    public void create(Event event) {
        ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.systemDefault());
        event.setDate(currentDateTime);
        event = eventDao.create(event);
        if (event.getName().isNotification()) {
            simpMessagingTemplate.convertAndSend("/topic/" + event.getAccountId(), eventDtoMapper.toDto(event));
        }
    }

    @Override
    public Page<EventDto> findAll(Pageable pageable, Map<String, ?> params, UserDetailsImpl user) {
        Page<Event> page = eventDao.findAll(pageable, params, user.getUser());
        List<EventDto> eventDtos = eventDtoMapper.toDtoList(page.getContent());
        populateAdditionalInfo(eventDtos, user.getUser().getAccountId());
        return new PageImpl<EventDto>(eventDtos, pageable, page.getTotalElements());
    }

    private void populateAdditionalInfo(List<EventDto> eventDtos, Long accountId) {
        List<Long> listAuthorsId = eventDtos.stream().map(EventDto::getAuthorId).distinct().collect(Collectors.toList());
        List<Long> listWarehousesId = eventDtos.stream().map(EventDto::getWarehouseId).distinct().collect(Collectors.toList());
        Map<Long, String> namesMap = userDao.findUsernames(accountId);
        for (EventDto eventDto : eventDtos) {
            eventDto.setAuthor(namesMap.get(eventDto.getAuthorId()));
        }

    }
}
