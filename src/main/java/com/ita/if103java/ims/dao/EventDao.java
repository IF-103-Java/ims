package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.Event;

import java.util.List;
import java.util.Map;

public interface EventDao {
    Event create(Event event);

    Event findById(Long id);

    List<Event> findAll(int limit, int offset, Map<String, ?> params);
}
