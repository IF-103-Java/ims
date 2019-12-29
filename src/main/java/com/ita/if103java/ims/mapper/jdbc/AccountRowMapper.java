package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.entity.Account;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class AccountRowMapper implements RowMapper<Account> {
    @Override
    public Account mapRow(ResultSet resultSet, int i) throws SQLException {
        Account account = new Account();
        account.setId(resultSet.getLong("id"));
        account.setName(resultSet.getString("name"));
        account.setTypeId(resultSet.getLong("type_id"));
        account.setCreatedDate(ZonedDateTime.of(resultSet.getObject("created_date", LocalDateTime.class), ZoneId.systemDefault()));
        account.setActive(resultSet.getBoolean("active"));
        return account;
    }
}
