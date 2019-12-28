package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.Consumer;

@Component
public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet resultSet, int i) throws SQLException {
        Event event = new Event();
        event.setId(resultSet.getLong("id"));
        event.setMessage(resultSet.getString("message"));
        event.setDate(ZonedDateTime.of(resultSet.getObject("date", LocalDateTime.class), ZoneId.systemDefault()));
        event.setAccountId(resultSet.getLong("account_id"));
        event.setAuthorId(resultSet.getLong("author_id"));
        event.setName(EventName.valueOf(resultSet.getString("name")));
        setValueOrNull(event::setWarehouseId, resultSet.getLong("warehouse_id"), resultSet);
        setValueOrNull(event::setTransactionId, resultSet.getLong("transaction_id"), resultSet);
        return event;
    }

    private <T> void setValueOrNull(Consumer<T> consumer, T value, ResultSet rs) throws SQLException {
        consumer.accept(rs.wasNull() ? null : value);
    }
}
