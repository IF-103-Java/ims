package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;

@Component
public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet resultSet, int i) throws SQLException {
        Event event = new Event();
        event.setId(resultSet.getLong("id"));
        event.setMessage(resultSet.getString("message"));
        event.setDate(resultSet.getObject("date", ZonedDateTime.class));
        event.setAccountId(resultSet.getLong("account_id"));
        event.setAuthorId(resultSet.getLong("author_id"));
        event.setTransactionId(resultSet.getLong("transaction_id"));
        event.setWarehouseId(resultSet.getLong("warehouse_id"));
        event.setType((EventType) resultSet.getObject("type"));
        return event;
    }
}
