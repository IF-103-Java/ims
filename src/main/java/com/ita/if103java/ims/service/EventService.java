package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.EventDto;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.security.UserDetailsImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface EventService {

    void create(Event event);

    List<EventDto> findAll(Pageable pageable, Map<String, ?> params, UserDetailsImpl user);

}
