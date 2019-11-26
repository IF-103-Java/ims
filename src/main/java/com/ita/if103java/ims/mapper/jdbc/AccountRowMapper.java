package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.entity.Account;
import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.entity.Type;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;

@Component
public class AccountRowMapper implements RowMapper<Account> {
    @Override
    public Account mapRow(ResultSet resultSet, int i) throws SQLException {
        Account account = new Account();
        account.setId(resultSet.getLong("id"));
        account.setName(resultSet.getString("name"));
        account.setType((AccountType) resultSet.getObject("type"));
        account.setAdminId(resultSet.getLong("admin_id"));
        account.setCreatedDate(resultSet.getObject("created_date", ZonedDateTime.class));
        account.setActive(resultSet.getBoolean("active"));
        return account;
    }
}
