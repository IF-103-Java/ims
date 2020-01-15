package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.UserDao;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.exception.dao.CRUDException;
import com.ita.if103java.ims.exception.dao.UserNotFoundException;
import com.ita.if103java.ims.mapper.jdbc.UserRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ita.if103java.ims.util.JDBCUtils.createWithAutogeneratedId;

@Repository
public class UserDaoImpl implements UserDao {

    private UserRowMapper userRowMapper;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDaoImpl(JdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
    }

    @Override
    public User create(User user) {
        try {
            user.setId(createWithAutogeneratedId(Long.class,
                (keyHolder) -> jdbcTemplate.update(connection -> getPreparedStatement(user, connection), keyHolder)));
            return user;
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `create` user {id = " + user.getId() + "}", e);
        }
    }

    @Override
    public User findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_USER_BY_ID, userRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException("Failed to obtain user during `select` {id = " + id + "}", e);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `select` user {id = " + id + "}", e);
        }

    }

    @Override
    public List<User> findUsersByAccountId(Long accountId) {
        try {
            return jdbcTemplate.query(Queries.SQL_SELECT_USERS_BY_ACCOUNT_ID, userRowMapper, accountId);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException("Failed to obtain users during `select` {accountId = " + accountId + "}", e);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `select` users {accountId = " + accountId + "}", e);
        }

    }

    @Override
    public User findAdminByAccountId(Long accountId) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_ADMIN_BY_ACCOUNT_ID, userRowMapper, accountId);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException("Failed to obtain user during `select` {accountId = " + accountId + "}", e);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `select` admin {accountId = " + accountId + "}", e);
        }
    }

    @Override
    public List<User> findAll(Pageable pageable) {
        try {
            return jdbcTemplate.query(Queries.SQL_SELECT_ALL_USERS, userRowMapper, pageable.getPageSize(), pageable.getOffset());
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `select * ` users ", e);
        }
    }

    @Override
    public Map<Long, String> findUserNames(Long accountId) {
        String where = String.format("account_id = " + accountId);
        return getNamesMap(where);
    }

    @Override
    public Map<Long, String> findUserNames(List<Long> idList) {
        String where = String.format("id IN (%s)", idList.toString().substring(1, idList.toString().length() - 1));
        return getNamesMap(where);
    }

    private Map<Long, String> getNamesMap(String where) {
        Map<Long, String> result = new HashMap<>();
        try {
            for (Map<String, Object> map : jdbcTemplate.queryForList(String.format(Queries.SQL_SELECT_USERNAMES, where))) {
                result.put(Long.valueOf(map.get("id").toString()), map.get("name").toString());
            }
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `select * ` users ", e);
        }
        return result;
    }

    @Override
    public User findByEmail(String email) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_USER_BY_EMAIL, userRowMapper, email);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException("Failed to obtain user during `select` {email = " + email + "}", e);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `select` user {email = " + email + "}", e);
        }
    }

    @Override
    public User update(User user) {
        int status;
        try {
            ZonedDateTime updatedDateTime = ZonedDateTime.now(ZoneId.systemDefault());
            status = jdbcTemplate.update(
                Queries.SQL_UPDATE_USER,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                Timestamp.from(updatedDateTime.toInstant()),
                user.isActive(),
                user.getEmailUUID(),
                user.getId());

            user.setUpdatedDate(updatedDateTime);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `update` user {id = " + user.getId() + "}", e);
        }
        if (status == 0) {
            throw new UserNotFoundException("Failed to obtain user during `update` {id = " + user.getId() + "}");
        }

        return user;
    }

    @Override
    public boolean updateAccountId(Long userId, Long accountId) {
        int status;
        try {
            status = jdbcTemplate.update(
                Queries.SQL_UPDATE_ACCOUNT_ID,
                accountId,
                userId);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `update` accountId {userId = " + userId + "}", e);
        }
        if (status == 0) {
            throw new UserNotFoundException("Failed to obtain user during `update` accountId {userId = " + userId + "}");
        }
        return true;
    }

    @Override
    public boolean softDelete(Long id) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_SET_ACTIVE_STATUS_USER, false, id);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during soft `delete` user {id = " + id + "}", e);
        }
        if (status == 0) {
            throw new UserNotFoundException("Failed to obtain user during soft `delete` {id = " + id + "}");
        }

        return true;
    }

    @Override
    public boolean hardDelete(Long id) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_DELETE_USER_BY_ID, id);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during hard `delete` user {id = " + id + "}", e);
        }
        if (status == 0) {
            throw new UserNotFoundException("Failed to obtain user during hard `delete` {id = " + id + "}");
        }

        return true;
    }


    @Override
    public boolean updatePassword(Long id, String newPassword) {
        int status;
        try {
            ZonedDateTime updatedDateTime = ZonedDateTime.now(ZoneId.systemDefault());
            status = jdbcTemplate.update(Queries.SQL_UPDATE_PASSWORD,
                newPassword,
                Timestamp.from(updatedDateTime.toInstant()),
                id);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `update` password {id = " + id + "}", e);
        }
        if (status == 0) {
            throw new UserNotFoundException("Failed to obtain user during `update` password {id = " + id + "}");
        }


        return true;
    }

    @Override
    public User findByEmailUUID(String emailUUID) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_USER_BY_EMAIL_UUID, userRowMapper, emailUUID);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException("Failed to obtain user during `select` {emailUUID = " + emailUUID + "}", e);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `select` user {emailUUID = " + emailUUID + "}", e);
        }
    }

    @Override
    public Integer countOfUsers(Long accountId) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_COUNT_OF_USERS, Integer.class, accountId);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException("Failed to obtain users during searching count of users. {id = " + accountId);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `select count(*)` of users {accountId = " + accountId + "}", e);
        }
    }

    private PreparedStatement getPreparedStatement(User user, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(Queries.SQL_CREATE_USER, Statement.RETURN_GENERATED_KEYS);

        int i = 0;
        preparedStatement.setString(++i, user.getFirstName());
        preparedStatement.setString(++i, user.getLastName());
        preparedStatement.setString(++i, user.getEmail());
        preparedStatement.setString(++i, user.getPassword());
        preparedStatement.setObject(++i, user.getRole().toString());
        preparedStatement.setObject(++i, Timestamp.from(user.getCreatedDate().toInstant()));
        preparedStatement.setObject(++i, Timestamp.from(user.getUpdatedDate().toInstant()));
        preparedStatement.setBoolean(++i, false);
        preparedStatement.setString(++i, user.getEmailUUID());
        preparedStatement.setObject(++i, user.getAccountId());

        return preparedStatement;
    }

    class Queries {

        public static final String SQL_CREATE_USER = """
                INSERT INTO users
                (first_name, last_name, email,  password, role,
                created_date, updated_date, active, email_uuid, account_id)
                VALUES(?,?,?,?,?,?,?,?,?,?)
            """;

        public static final String SQL_SELECT_USER_BY_ID = """
                SELECT *
                FROM users
                WHERE id = ?
            """;

        public static final String SQL_SELECT_USER_BY_EMAIL = """
                SELECT *
                FROM users
                WHERE email = ?
            """;

        public static final String SQL_SELECT_ALL_USERS = """
                SELECT *
                FROM users
                Limit ?
                Offset ?
            """;

        public static final String SQL_SELECT_USERS_BY_ACCOUNT_ID = """
                SELECT *
                FROM users
                WHERE account_id = ?
            """;

        public static final String SQL_SELECT_ADMIN_BY_ACCOUNT_ID = """
                SELECT *
                FROM users
                WHERE role = 'ROLE_ADMIN'
                AND account_id = ?
            """;

        public static final String SQL_UPDATE_USER = """
                UPDATE users
                SET first_name= ?, last_name = ?,
                email = ?, password = ?, updated_date = ?,
                active = ?, email_uuid = ?
                WHERE id = ?
            """;

        public static final String SQL_UPDATE_ACCOUNT_ID = """
                UPDATE users
                SET account_id = ?
                WHERE id = ?
            """;

        public static final String SQL_SET_ACTIVE_STATUS_USER = """
                UPDATE users
                SET active = ?
                WHERE id = ?
            """;

        public static final String SQL_UPDATE_PASSWORD = """
                UPDATE users
                SET  password = ?, updated_date = ?
                WHERE id = ?
            """;

        public static final String SQL_SELECT_USER_BY_EMAIL_UUID = """
                SELECT *
                FROM users
                WHERE email_uuid = ?
            """;

        public static final String SQL_DELETE_USER_BY_ID = """
                DELETE
                FROM users
                WHERE id = ?
            """;

        public static final String SQL_COUNT_OF_USERS = """
                SELECT COUNT(*)
                FROM users
                WHERE account_id = ?
            """;

        public static final String SQL_SELECT_USERNAMES = """
                SELECT id, CONCAT(first_name, \" \", last_name) AS name
                FROM users
                WHERE %s
            """;
    }
}
