package com.ita.if103java.ims.dao.impl;


import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.entity.Account;
import com.ita.if103java.ims.exception.CRUDException;
import com.ita.if103java.ims.exception.EntityNotFoundException;
import com.ita.if103java.ims.mapper.jdbc.AccountRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@Repository
public class AccountDaoImpl implements AccountDao {

    private static Logger logger = LoggerFactory.getLogger(AccountDaoImpl.class);
    private JdbcTemplate jdbcTemplate;
    private AccountRowMapper accountRowMapper;

    @Autowired
    public AccountDaoImpl(DataSource dataSource, AccountRowMapper accountRowMapper) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.accountRowMapper = accountRowMapper;
    }

    @Override
    public Account create(Account account) {
        try {
            ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.systemDefault());
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> createStatement(account, currentDateTime, connection), holder);
            account.setId(Optional.ofNullable(holder.getKey())
                .map(Number::longValue)
                .orElseThrow(() -> new CRUDException("Error during an account creation: " +
                    "Autogenerated key is null")));
            account.setCreatedDate(currentDateTime);
            return account;
        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "create", "id = " + account.getId());
        }
    }

    @Override
    public Account findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_ACCOUNT_BY_ID, accountRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw accountEntityNotFoundException(e.getMessage(), "id = " + id);
        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "get", "id = " + id);
        }
    }

    @Override
    public Account findByAdminId(Long adminId) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_ACCOUNT_BY_ADMIN_ID, accountRowMapper, adminId);
        } catch (EmptyResultDataAccessException e) {
            throw accountEntityNotFoundException(e.getMessage(), "id = " + adminId);
        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "get", "id = " + adminId);
        }
    }

    @Override
    public Account update(Account account) {
        int status;
        try {
            status = jdbcTemplate.update(
                Queries.SQL_UPDATE_ACCOUNT,
                account.getName(),
                account.getId());

        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "update", "id = " + account.getId());
        }
        if (status == 0)
            throw accountEntityNotFoundException("Update account exception", "id = " + account.getId());

        return account;
    }

    @Override
    public boolean activate(Long id) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_SET_ACTIVE_STATUS_ACCOUNT, true, id);

        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "activate", "id = " + id);
        }
        if (status == 0)
            throw accountEntityNotFoundException("Activate account exception", "id = " + id);

        return true;
    }

    @Override
    public boolean delete(Long id) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_SET_ACTIVE_STATUS_ACCOUNT, false, id);

        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "delete", "id = " + id);
        }
        if (status == 0)
            throw accountEntityNotFoundException("Delete account exception", "id = " + id);

        return true;
    }

    @Override
    public boolean updateToPremium(Long id, Long type_id) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_UPDATE_ACCOUNT_TO_PREMIUM, type_id, id);

        } catch (DataAccessException e) {
            throw crudException(e.getMessage(), "update", "id = " + id);
        }
        if (status == 0)
            throw accountEntityNotFoundException("Update account to Premium exception", "id = " + id);

        return true;
    }

    private PreparedStatement createStatement(Account account, ZonedDateTime currentDateTime, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(Queries.SQL_CREATE_ACCOUNT, Statement.RETURN_GENERATED_KEYS);

        int i = 0;
        preparedStatement.setString(++i, account.getName());
        preparedStatement.setLong(++i, account.getTypeId());
        preparedStatement.setLong(++i, account.getAdminId());
        preparedStatement.setObject(++i, currentDateTime.toLocalDateTime());
        preparedStatement.setBoolean(++i, account.isActive());


        return preparedStatement;
    }

    private EntityNotFoundException accountEntityNotFoundException(String message, String attribute) {
        EntityNotFoundException exception = new EntityNotFoundException(message);
        logger.error("EntityNotFoundException exception. Account is not found ({}). Message: {}", attribute, message);
        return exception;
    }

    private CRUDException crudException(String message, String operation, String attribute) {
        CRUDException exception = new CRUDException(message);
        logger.error("CRUDException exception. Operation:({}) account ({}) exception. Message: {}", operation, attribute, message);
        return exception;
    }

    class Queries {

        static final String SQL_CREATE_ACCOUNT = "" +
            "INSERT INTO accounts (name, type_id, admin_id, created_date, active)" +
            "VALUES(?,?,?,?,?)";

        static final String SQL_SELECT_ACCOUNT_BY_ID = "SELECT * FROM accounts WHERE id = ?";

        static final String SQL_SELECT_ACCOUNT_BY_ADMIN_ID = "SELECT * FROM accounts WHERE admin_id = ?";

        static final String SQL_UPDATE_ACCOUNT = "UPDATE accounts SET " +
            "name = ? WHERE id = ?";

        static final String SQL_UPDATE_ACCOUNT_TO_PREMIUM = "UPDATE accounts SET " +
            "type_id = ? WHERE id = ?";

        static final String SQL_SET_ACTIVE_STATUS_ACCOUNT = "UPDATE accounts SET active = ? WHERE id = ?";
    }
}
