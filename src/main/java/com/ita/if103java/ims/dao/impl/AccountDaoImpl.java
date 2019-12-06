package com.ita.if103java.ims.dao.impl;


import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.entity.Account;
import com.ita.if103java.ims.exception.AccountNotFoundException;
import com.ita.if103java.ims.exception.CRUDException;
import com.ita.if103java.ims.mapper.jdbc.AccountRowMapper;
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

    private JdbcTemplate jdbcTemplate;
    private AccountRowMapper accountRowMapper;
    private AccountTypeDaoImpl accountTypeDaoImpl;

    @Autowired
    public AccountDaoImpl(DataSource dataSource, AccountRowMapper accountRowMapper, AccountTypeDaoImpl accountTypeDaoImpl) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.accountRowMapper = accountRowMapper;
        this.accountTypeDaoImpl = accountTypeDaoImpl;
    }

    @Override
    public Account create(Account account) {
        try {
            ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.systemDefault());
            Long typeId = accountTypeDaoImpl.minLvlType();
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> createStatement(account, typeId, currentDateTime, connection), holder);
            account.setId(Optional.ofNullable(holder.getKey())
                .map(Number::longValue)
                .orElseThrow(() -> new CRUDException("Error during an account creation: " +
                    "Autogenerated key is null")));
            account.setTypeId(typeId);
            account.setCreatedDate(currentDateTime);
            account.setActive(true);
            return account;
        } catch (DataAccessException e) {
            throw new CRUDException("Error during create account, id = " + account.getId(), e);
        }
    }

    @Override
    public Account findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_ACCOUNT_BY_ID, accountRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new AccountNotFoundException("id = " + id, e);
        } catch (DataAccessException e) {
            throw new CRUDException("get, id = " + id, e);
        }
    }

    @Override
    public Account findByAdminId(Long adminId) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_ACCOUNT_BY_ADMIN_ID, accountRowMapper, adminId);
        } catch (EmptyResultDataAccessException e) {
            throw new AccountNotFoundException("id = " + adminId, e);
        } catch (DataAccessException e) {
            throw new CRUDException("get, id = " + adminId, e);
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
            throw new CRUDException("update, id = " + account.getId(), e);
        }
        if (status == 0)
            throw new AccountNotFoundException("Update account exception, id = " + account.getId());

        return account;
    }

    @Override
    public boolean activate(Long id) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_SET_ACTIVE_STATUS_ACCOUNT, true, id);

        } catch (DataAccessException e) {
            throw new CRUDException("activate, id = " + id, e);
        }
        if (status == 0)
            throw new AccountNotFoundException("Activate account exception, id = " + id);

        return true;
    }

    @Override
    public boolean delete(Long id) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_SET_ACTIVE_STATUS_ACCOUNT, false, id);

        } catch (DataAccessException e) {
            throw new CRUDException("delete, id = " + id, e);
        }
        if (status == 0)
            throw new AccountNotFoundException("Delete account exception, id = " + id);

        return true;
    }

    @Override
    public boolean upgradeAccount(Long id, Long typeId) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_UPGRADE_ACCOUNT, typeId, id);

        } catch (DataAccessException e) {
            throw new CRUDException("update, id = " + id, e);
        }
        if (status == 0)
            throw new AccountNotFoundException("Update account to Premium exception, id = " + id);

        return true;
    }

    private PreparedStatement createStatement(Account account, Long typeId, ZonedDateTime currentDateTime, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(Queries.SQL_CREATE_ACCOUNT, Statement.RETURN_GENERATED_KEYS);

        int i = 0;
        preparedStatement.setString(++i, account.getName());
        preparedStatement.setLong(++i, typeId);
        preparedStatement.setLong(++i, account.getAdminId());
        preparedStatement.setObject(++i, currentDateTime.toLocalDateTime());
        preparedStatement.setBoolean(++i, true);


        return preparedStatement;
    }

    class Queries {

        static final String SQL_CREATE_ACCOUNT = "" +
            "INSERT INTO accounts (name, type_id, admin_id, created_date, active)" +
            "VALUES(?,?,?,?,?)";

        static final String SQL_SELECT_ACCOUNT_BY_ID = "SELECT * FROM accounts WHERE id = ?";

        static final String SQL_SELECT_ACCOUNT_BY_ADMIN_ID = "SELECT * FROM accounts WHERE admin_id = ?";

        static final String SQL_UPDATE_ACCOUNT = "UPDATE accounts SET " +
            "name = ? WHERE id = ?";

        static final String SQL_UPGRADE_ACCOUNT = "UPDATE accounts SET " +
            "type_id = ? WHERE id = ?";

        static final String SQL_SET_ACTIVE_STATUS_ACCOUNT = "UPDATE accounts SET active = ? WHERE id = ?";
    }
}
