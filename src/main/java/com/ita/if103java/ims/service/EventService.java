package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.EventDto;
import com.ita.if103java.ims.entity.Event;

import java.util.List;
import java.util.Map;

public interface EventService {
    void create(Event event);

    void create(EventDto eventDto);

    EventDto findById(Long id);

    List<EventDto> findAll(Map<String, ?> params);
}
