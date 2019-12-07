package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.EventDao;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.EventType;
import com.ita.if103java.ims.exception.CRUDException;
import com.ita.if103java.ims.exception.EventNotFoundException;
import com.ita.if103java.ims.mapper.jdbc.EventRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class EventDaoImpl implements EventDao {
    private static final Logger logger = LoggerFactory.getLogger(EventDaoImpl.class);
    private JdbcTemplate jdbcTemplate;
    private EventRowMapper eventRowMapper;

    @Autowired
    public EventDaoImpl(JdbcTemplate jdbcTemplate, EventRowMapper eventRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.eventRowMapper = eventRowMapper;
    }

    @Override
    public Event create(Event event) {
        try {
            final KeyHolder keyHolder = new GeneratedKeyHolder();
            event.setDate(ZonedDateTime.now(ZoneId.systemDefault()));
            jdbcTemplate.update(connection -> getPreparedStatement(connection, event), keyHolder);
            event.setId(Optional.ofNullable(keyHolder.getKey())
                .map(Number::longValue)
                .orElseThrow(() -> new CRUDException("Error during an event creation. Autogenerated key is null")));
            return event;
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `create` {id = " + event.getId() + "}EventDao.create", e);
        }
    }

    @Override
    public Event findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_EVENT_BY_ID, eventRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EventNotFoundException("Failed to obtain event during `select` {id = " + id + "}, EventDao.findById", e);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `select` {id = " + id + "}, EventDao.findById", e);
        }
    }

    public List<Event> findAll(Map<String, ?> params) {
        final String where = Stream
            .of("account_id", "warehouse_id", "author_id", "name", "type", "date", "after", "before")
            .filter(params::containsKey)
            .map(x -> buildSqlFilterCondition(x, params.get(x)))
            .collect(Collectors.joining("\n and "));
        final String query = String.format("select * from events where %s",
            where.isBlank() ? "TRUE" : where);
        try {
            System.out.println(query);
            return jdbcTemplate.query(query, eventRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new EventNotFoundException("Failed to obtain event during `select` {" + where + "}, EventDao.findAll", e);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `select` {" + where + "}, EventDao.findAll", e);
        }
    }

    private String buildSqlFilterCondition(String columnName, Object columnValue) {
        if (columnValue instanceof List && columnName != "type") {
            String values = "";
            for (Object value : (List<Object>) columnValue) {
                System.out.println(value);
                values = values.concat("'" + value + "', ");
            }
            values = values.substring(0, values.length() - 2);
            return String.format("%s in (%s)", columnName, values);
        }
        if (columnName.equals("type")) {
            List<EventType> types = new LinkedList<>();
            if (columnValue instanceof List) {
                for (Object type : (List) columnValue) {
                    types.add(EventType.valueOf(type.toString()));
                }
            } else {
                types.add(EventType.valueOf(columnValue.toString()));
            }
            List<EventName> names = new LinkedList<>();
            for (EventName name : EventName.values()) {
                if (types.contains(name.getType())) {
                    names.add(name);
                }
            }
            System.out.println(names);
            return buildSqlFilterCondition("name", names);
        }
        if (columnName.equals("date")) {
            return String.format("DATE(date) = '%s'", columnValue);
        }
        if (columnName.equals("after")) {
            return String.format("DATE(date) >= '%s'", columnValue);
        }
        if (columnName.equals("before")) {
            return String.format("DATE(date) <= '%s'", columnValue);
        }
        return String.format("%s %s '%s'", columnName, "=", columnValue);
    }

    private PreparedStatement getPreparedStatement(Connection connection, Event event) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(Queries.SQL_CREATE_EVENT, Statement.RETURN_GENERATED_KEYS);
        int i = 0;
        preparedStatement.setString(++i, event.getMessage());
        preparedStatement.setObject(++i, event.getDate().toLocalDateTime());
        preparedStatement.setLong(++i, event.getAccountId());
        preparedStatement.setLong(++i, event.getAuthorId());
        preparedStatement.setObject(++i, event.getWarehouseId() != null ? event.getWarehouseId() : null);
        preparedStatement.setString(++i, event.getName().toString());
        preparedStatement.setObject(++i, event.getTransactionId() != null ? event.getTransactionId() : null);
        return preparedStatement;
    }

    class Queries {

        static final String SQL_CREATE_EVENT = "" +
            "INSERT INTO events(message, date, account_id, author_id, warehouse_id, name, transaction_id)" +
            "VALUES(?,?,?,?,?,?,?)";

        static final String SQL_SELECT_EVENT_BY_ID = "SELECT * FROM events WHERE id = ?";
    }
}
