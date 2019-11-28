package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.EventDao;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventType;
import com.ita.if103java.ims.exception.CRUDException;
import com.ita.if103java.ims.exception.EntityNotFoundException;
import com.ita.if103java.ims.mapper.jdbc.EventRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public class EventDaoImpl implements EventDao {
    private static Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);
    private JdbcTemplate jdbcTemplate;
    private EventRowMapper eventRowMapper;

    @Autowired
    public EventDaoImpl(JdbcTemplate jdbcTemplate, EventRowMapper eventRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.eventRowMapper = eventRowMapper;
    }

    @Override
    public boolean create(Event event) {
        try {
            return jdbcTemplate.update(Queries.SQL_CREATE_EVENT, event.getMessage(), event.getDate().toLocalDateTime(),
                event.getAccountId(), event.getAuthorId(), event.getWarehouseId(), event.getType().toString(),
                event.getTransactionId()) > 0;
        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "create", "id = " + event.getId());
        }
    }

    @Override
    public Event findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_EVENT_BY_ID, eventRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw eventEntityNotFoundException(e.getMessage(), "id = " + id);
        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "get", "id = " + id);
        }
    }

    @Override
    public Event findByTransactionId(Long transactionId) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_EVENT_BY_TRANSACTION_ID, eventRowMapper, transactionId);
        } catch (EmptyResultDataAccessException e) {
            throw eventEntityNotFoundException(e.getMessage(), "id = " + transactionId);
        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "get", "id = " + transactionId);
        }
    }

    @Override
    public List<Event> findByAccountId(Long accountId) {
        try {
            return jdbcTemplate.query(Queries.SQL_SELECT_EVENT_BY_ACCOUNT_ID, eventRowMapper, accountId);
        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "get", "*");
        }
    }

    @Override
    public List<Event> findByWarehouseId(Long warehouseId) {
        try {
            return jdbcTemplate.query(Queries.SQL_SELECT_EVENTS_BY_WAREHOUSE_ID, eventRowMapper, warehouseId);
        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "get", "*");
        }
    }

    @Override
    public List<Event> findByAuthorId(Long authorId) {
        try {
            return jdbcTemplate.query(Queries.SQL_SELECT_EVENTS_BY_AUTHOR_ID, eventRowMapper, authorId);
        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "get", "*");
        }
    }

    @Override
    public List<Event> findByAccountIdAndType(Long accountId, EventType type) {
        try {
            return jdbcTemplate.query(Queries.SQL_SELECT_EVENTS_BY_ACCOUNT_ID_AND_TYPE, eventRowMapper, accountId, type.toString());
        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "get", "*");
        }
    }

    @Override
    public List<Event> findByWarehouseIdAndType(Long warehouseId, EventType type) {
        try {
            return jdbcTemplate.query(Queries.SQL_SELECT_EVENTS_BY_WAREHOUSE_ID_AND_TYPE, eventRowMapper, warehouseId, type.toString());
        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "get", "*");
        }
    }

    @Override
    public List<Event> findByWarehouseIdAndDate(Long warehouseId, ZonedDateTime date) {
        try {
            return jdbcTemplate.query(Queries.SQL_SELECT_EVENTS_BY_WAREHOUSE_ID_AND_DATE, eventRowMapper, warehouseId, date.toLocalDate().toString());
        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "get", "*");
        }
    }

    @Override
    public List<Event> findByAccountIdAndDate(Long accountId, ZonedDateTime date) {
        try {
            return jdbcTemplate.query(Queries.SQL_SELECT_EVENTS_BY_ACCOUNT_ID_AND_DATE, eventRowMapper, accountId, date.toLocalDate().toString());
        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "get", "*");
        }
    }

    @Override
    public List<Event> findByWarehouseIdAndWithinDates(Long warehouseId, ZonedDateTime after, ZonedDateTime before) {
        try {
            return jdbcTemplate.query(Queries.SQL_SELECT_EVENTS_BY_WAREHOUSE_ID_AND_WITHIN_DATES, eventRowMapper,
                warehouseId, after.toLocalDate().toString(), before.toLocalDate().toString());
        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "get", "*");
        }
    }

    @Override
    public List<Event> findByAccountIdAndWithinDates(Long accountId, ZonedDateTime after, ZonedDateTime before) {
        try {
            return jdbcTemplate.query(Queries.SQL_SELECT_EVENTS_BY_ACCOUNT_ID_AND_WITHIN_DATES, eventRowMapper, accountId,
                after.toLocalDate().toString(), before.toLocalDate().toString());
        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "get", "*");
        }
    }

    private EntityNotFoundException eventEntityNotFoundException(String message, String attribute) {
        EntityNotFoundException exception = new EntityNotFoundException(message);
        logger.error("EntityNotFoundException exception. Event is not found ({}). Message: {}", attribute, message);
        return exception;
    }

    private CRUDException crudException(String message, String operation, String attribute) {
        CRUDException exception = new CRUDException(message);
        logger.error("CRUDException exception. Operation:({}) event ({}) exception. Message: {}", operation, attribute, message);
        return exception;
    }

    class Queries {

        static final String SQL_CREATE_EVENT = "" +
            "INSERT INTO events(message, date, account_id, author_id, warehouse_id, type, transaction_id)" +
            "VALUES(?,?,?,?,?,?,?)";

        static final String SQL_SELECT_EVENT_BY_ID = "SELECT* FROM events WHERE id = ?";

        static final String SQL_SELECT_EVENT_BY_ACCOUNT_ID = "SELECT* FROM events WHERE account_id = ?";

        static final String SQL_SELECT_EVENT_BY_TRANSACTION_ID = "SELECT* FROM events WHERE transaction_id = ?";

        static final String SQL_SELECT_EVENTS_BY_WAREHOUSE_ID = "SELECT* FROM events WHERE warehouse_id = ?";

        static final String SQL_SELECT_EVENTS_BY_AUTHOR_ID = "SELECT* FROM events WHERE author_id = ?";

        static final String SQL_SELECT_EVENTS_BY_ACCOUNT_ID_AND_TYPE = "SELECT* FROM events WHERE account_id = ? AND type = ?";

        static final String SQL_SELECT_EVENTS_BY_WAREHOUSE_ID_AND_TYPE = "SELECT* FROM events WHERE warehouse_id = ? AND type = ?";

        static final String SQL_SELECT_EVENTS_BY_WAREHOUSE_ID_AND_DATE = "SELECT* FROM events WHERE warehouse_id = ? AND DATE(date) = ?";

        static final String SQL_SELECT_EVENTS_BY_ACCOUNT_ID_AND_DATE = "SELECT* FROM events WHERE account_id = ? AND DATE(date) = ?";

        static final String SQL_SELECT_EVENTS_BY_ACCOUNT_ID_AND_WITHIN_DATES = "SELECT* FROM events WHERE account_id = ? AND DATE(date) >= ? AND DATE(date) <= ?";

        static final String SQL_SELECT_EVENTS_BY_WAREHOUSE_ID_AND_WITHIN_DATES = "SELECT* FROM events WHERE warehouse_id = ? AND DATE(date) >= ? AND DATE(date) <= ?";
    }
}
