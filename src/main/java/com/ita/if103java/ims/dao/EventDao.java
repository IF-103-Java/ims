package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventType;

import java.time.ZonedDateTime;
import java.util.List;

public interface EventDao {

    Event create(Event event);

    Event findById(Long id);

    Event findByTransactionId(Long transactionId);

    List<Event> findByAccountId(Long accountId);

    List<Event> findByWarehouseId(Long warehouseId);

    List<Event> findByAuthorId(Long authorId);

    List<Event> findByAccountIdAndType(Long accountId, EventType type);

    List<Event> findByWarehouseIdAndType(Long warehouseId, EventType type);

    List<Event> findByWarehouseIdAndDate(Long warehouseId, ZonedDateTime date);

    List<Event> findByAccountIdAndDate(Long accountId, ZonedDateTime date);

    List<Event> findByWarehouseIdAndWithinDates(Long warehouseId, ZonedDateTime after, ZonedDateTime before);

    List<Event> findByAccountIdAndWithinDates(Long accountId, ZonedDateTime after, ZonedDateTime before);
}
