package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface EventDao {

    Event create(Event event);

    Page<Event> findAll(Pageable pageable, Map<String, ?> params, User user);

    void deleteByAccountId(Long accountId);
}
