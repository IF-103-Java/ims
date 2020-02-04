package com.ita.if103java.ims.dao.impl;


import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.entity.Account;
import com.ita.if103java.ims.exception.dao.AccountNotFoundException;
import com.ita.if103java.ims.exception.dao.CRUDException;
import com.ita.if103java.ims.mapper.jdbc.AccountRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.ita.if103java.ims.util.JDBCUtils.createWithAutogeneratedId;

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
            account.setTypeId(typeId);
            account.setCreatedDate(currentDateTime);
            account.setId(createWithAutogeneratedId(Long.class,
                (keyHolder) -> jdbcTemplate.update(connection -> getPreparedStatement(account, connection), keyHolder)));
            return account;
        } catch (DataAccessException e) {
            throw new CRUDException("Error during an account creation: {id = " + account.getId() + " }. ", e);
        }
    }

    @Override
    public Account findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_ACCOUNT_BY_ID, accountRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new AccountNotFoundException("id = " + id, e);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during searching by id, id = " + id, e);
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
            throw new CRUDException("Update account exception, id = " + account.getId(), e);
        }
        if (status == 0)
            throw new AccountNotFoundException("Failed to obtain account during 'update' {id = " + account.getId());

        return account;
    }

    @Override
    public boolean activate(Long id) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_SET_ACTIVE_STATUS_ACCOUNT, true, id);

        } catch (DataAccessException e) {
            throw new CRUDException("Activate account exception, id = " + id, e);
        }
        if (status == 0)
            throw new AccountNotFoundException("Failed to obtain account during 'activate' {id = " + id);

        return true;
    }

    @Override
    public boolean delete(Long id) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_SET_ACTIVE_STATUS_ACCOUNT, false, id);

        } catch (DataAccessException e) {
            throw new CRUDException("Delete account exception, id = " + id, e);
        }
        if (status == 0)
            throw new AccountNotFoundException("Failed to obtain account during 'delete' {id = " + id);

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

    @Override
    public boolean hardDelete(Long accountId) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_HARD_DELETE, accountId);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during hard `delete` account {id = " + accountId + "}", e);
        }
        if (status == 0) {
            throw new AccountNotFoundException("Failed to obtain account during hard `delete` {id = " + accountId + "}");
        }
        return true;
    }

    private PreparedStatement getPreparedStatement(Account account, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(Queries.SQL_CREATE_ACCOUNT, Statement.RETURN_GENERATED_KEYS);

        int i = 0;
        preparedStatement.setString(++i, account.getName());
        preparedStatement.setLong(++i, account.getTypeId());
        preparedStatement.setObject(++i, Timestamp.from(account.getCreatedDate().toInstant()));
        preparedStatement.setBoolean(++i, account.isActive());


        return preparedStatement;
    }

    class Queries {

        static final String SQL_CREATE_ACCOUNT = """
                INSERT INTO accounts
                (name, type_id, created_date, active)
                VALUES(?,?,?,?)
            """;

        static final String SQL_SELECT_ACCOUNT_BY_ID = """
                SELECT *
                FROM accounts
                WHERE id = ?
            """;

        static final String SQL_UPDATE_ACCOUNT = """
                UPDATE accounts
                SET name = ?
                WHERE id = ?
            """;

        static final String SQL_UPGRADE_ACCOUNT = """
                UPDATE accounts
                SET type_id = ?
                WHERE id = ?
            """;

        static final String SQL_SET_ACTIVE_STATUS_ACCOUNT = """
                UPDATE accounts
                SET active = ?
                WHERE id = ?
            """;

        static final String SQL_HARD_DELETE = """
                DELETE
                FROM accounts
                WHERE id = ?
            """;
    }
}
