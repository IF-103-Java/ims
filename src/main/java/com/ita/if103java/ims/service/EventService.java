package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.EventDto;

import java.util.List;
import java.util.Map;

public interface EventService {
    EventDto create(EventDto eventDto);

    EventDto findById(Long id);

    List<EventDto> findAll(Map<String, ?> params);
}
