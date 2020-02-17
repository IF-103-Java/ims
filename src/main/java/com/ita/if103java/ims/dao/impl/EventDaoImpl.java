package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.config.GeneratedKeyHolderFactory;
import com.ita.if103java.ims.dao.EventDao;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.EventType;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.exception.dao.CRUDException;
import com.ita.if103java.ims.exception.dao.EventNotFoundException;
import com.ita.if103java.ims.mapper.jdbc.EventRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ita.if103java.ims.util.JDBCUtils.createWithAutogeneratedId;

@Repository
public class EventDaoImpl implements EventDao {

    private JdbcTemplate jdbcTemplate;
    private EventRowMapper eventRowMapper;
    private GeneratedKeyHolderFactory generatedKeyHolderFactory;

    @Autowired
    public EventDaoImpl(JdbcTemplate jdbcTemplate,
                        EventRowMapper eventRowMapper,
                        GeneratedKeyHolderFactory generatedKeyHolderFactory) {
        this.jdbcTemplate = jdbcTemplate;
        this.eventRowMapper = eventRowMapper;
        this.generatedKeyHolderFactory = generatedKeyHolderFactory;
    }

    @Override
    public Event create(Event event) {
        try {
            event.setId(
                createWithAutogeneratedId(Long.class, (keyHolder) ->
                        jdbcTemplate.update(connection -> getPreparedStatement(connection, event), keyHolder),
                    generatedKeyHolderFactory.newKeyHolder())
            );
            return event;
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `create` {id = " + event.getId() + "}EventDao.create", e);
        }
    }

    @Override
    public Page<Event> findAll(Pageable pageable, Map<String, Object> params, User user) {
        if (params.containsKey("name")) {
            params.remove("type");
        }
        String accountCondition = buildSqlCondition("account_id", user.getAccountId());
        String personalConditions = "";
        if (user.getRole().equals(Role.ROLE_WORKER)) {
            if (params.containsKey("name")) {
                personalConditions = buildSqlNameCondition(params, user.getId());
            } else if (params.containsKey("type")) {
                personalConditions = buildSqlTypeCondition(params, user.getId());
            } else {
                personalConditions = "("
                    .concat(buildSqlPrivacyCondition())
                    .concat(" or ")
                    .concat(buildSqlDefaultCondition(user.getId()))
                    .concat(")");
            }
        }

        String typeAndNameConditions = Stream
            .of("type", "name")
            .filter(params::containsKey)
            .map(x -> buildSqlCondition(x, params.get(x)))
            .collect(Collectors.joining("\n and "));

        String conditions = Stream
            .of("warehouse_id", "author_id", "date", "after", "before")
            .filter(params::containsKey)
            .map(x -> buildSqlCondition(x, params.get(x)))
            .collect(Collectors.joining("\n and "));

        String where;
        if (typeAndNameConditions.isBlank()) {
            where = personalConditions;
        } else {
            if (!personalConditions.isBlank()) {
                where = "(" + typeAndNameConditions + " or " + personalConditions + ")";
            } else {
                where = typeAndNameConditions;
            }
        }
        if (!conditions.isBlank()) {
            if (!where.isBlank()) {
                where = conditions + " and " + where;
            } else {
                where = conditions;
            }
        }
        where = where.isBlank() ? accountCondition : accountCondition.concat(" and " + where);
        String sort = pageable.getSort().toString().replaceAll(": ", " ");
        final String querySelectEvents = String.format("""
                select * from events where %s ORDER BY %s Limit %s OFFSET %s
                """,
            where, sort, pageable.getPageSize(), pageable.getOffset());
        final String rowCountSql = String.format("""
            select count(1) from events where %s
            """, where);
        try {
            List<Event> events = jdbcTemplate.query(querySelectEvents, eventRowMapper);
            Integer rowCount = jdbcTemplate.queryForObject(rowCountSql, Integer.class);
            return new PageImpl<>(events, pageable, rowCount);
        } catch (EmptyResultDataAccessException e) {
            throw new EventNotFoundException("Failed to obtain event during " + querySelectEvents + ", EventDao.findAll", e);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during  " + querySelectEvents + ", EventDao.findAll", e);
        }
    }

    private String buildSqlNameCondition(Map<String, Object> params, Long userId) {
        String condition = "";
        if (params.get("name") instanceof Collection) {
            List<String> names = new ArrayList<>((Collection<String>) params.get("name"));
            List<String> userEventNames = new ArrayList<>();
            for (String name : names) {
                if (EventName.valueOf(name).getType().equals(EventType.USER)) {
                    userEventNames.add(name);
                }
            }
            for (String name : userEventNames) {
                names.remove(name);
            }
            if (names.isEmpty()) {
                params.remove("name");
            } else {
                params.put("name", names);
            }
            Collections.sort(userEventNames);
            if (!userEventNames.isEmpty()) {
                condition = "("
                    .concat(buildSqlCondition("name", userEventNames))
                    .concat(" and ")
                    .concat(buildSqlCondition("author_id", userId))
                    .concat(")");
            }
        } else if (EventName.valueOf((String) params.get("name")).getType().equals(EventType.USER)) {
            condition = "("
                .concat(buildSqlCondition("name", params.get("name")))
                .concat(" and ")
                .concat(buildSqlCondition("author_id", userId))
                .concat(")");
            params.remove("name");
        }
        return condition;
    }

    private String buildSqlTypeCondition(Map<String, ?> params, Long userId) {
        String condition = "";
        if (params.get("type") instanceof Collection && ((Collection) params.get("type")).contains("USER")) {
            ((Collection) params.get("type")).remove("USER");
            if (((Collection) params.get("type")).size() > 0) {
                condition = buildSqlDefaultCondition(userId);
            } else {
                params.remove("type");
                condition = buildSqlDefaultCondition(userId);
            }
        } else if (params.get("type").equals("USER")) {
            params.remove("type");
            condition = buildSqlDefaultCondition(userId);
        }
        return condition;
    }

    private String buildSqlPrivacyCondition() {
        return "("
            .concat(buildSqlCondition("type", EventType.USER))
            .concat(")")
            .replace("in", "not in");
    }

    private String buildSqlDefaultCondition(Long userId) {
        return "("
            .concat(buildSqlCondition("type", EventType.USER))
            .concat(" and ")
            .concat(buildSqlCondition("author_id", userId))
            .concat(")");
    }

    private String buildSqlCondition(String columnName, Object columnValue) {
        if (columnValue instanceof Collection && !columnName.equals("type")) {
            StringJoiner values = new StringJoiner("', '", "'", "'");
            for (Object value : (Collection<Object>) columnValue) {
                values.add(value.toString());
            }
            return String.format("%s in (%s)", columnName, values);
        }

        if (columnName.equals("type")) {
            Set<EventName> names = new TreeSet<>(new Comparator<EventName>() {
                @Override
                public int compare(EventName o1, EventName o2) {
                    return o1.getLabel().compareTo(o2.getLabel());
                }
            });
            if (columnValue instanceof Collection) {
                for (Object type : (Collection) columnValue) {
                    names.addAll(EventName.getValuesByType(EventType.valueOf(type.toString())));
                }
            } else {
                names.addAll(EventName.getValuesByType(EventType.valueOf(columnValue.toString())));
            }

            return buildSqlCondition("name", names);
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

    @Override
    public void deleteByAccountId(Long accountId) {
        try {
            jdbcTemplate.update(Queries.SQL_DELETE_BY_ACCOUNT_ID, accountId);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during  " + Queries.SQL_DELETE_BY_ACCOUNT_ID + accountId +
                ", EventDao.deleteByAccountId", e);
        }
    }

    private PreparedStatement getPreparedStatement(Connection connection, Event event) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(Queries.SQL_CREATE_EVENT, Statement.RETURN_GENERATED_KEYS);
        int i = 0;
        preparedStatement.setString(++i, event.getMessage());
        preparedStatement.setObject(++i, event.getDate().toLocalDateTime());
        preparedStatement.setLong(++i, event.getAccountId());
        preparedStatement.setLong(++i, event.getAuthorId());
        preparedStatement.setObject(++i, event.getWarehouseId());
        preparedStatement.setString(++i, event.getName().toString());
        preparedStatement.setObject(++i, event.getTransactionId());
        return preparedStatement;
    }

    class Queries {

        static final String SQL_CREATE_EVENT = """
                INSERT INTO events
                (message, date, account_id, author_id, warehouse_id, name, transaction_id)
                VALUES(?,?,?,?,?,?,?)
            """;

        static final String SQL_DELETE_BY_ACCOUNT_ID = """
                DELETE
                FROM events
                WHERE account_id = ?
            """;
    }
}
