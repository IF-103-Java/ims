package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setFirstName(resultSet.getString("first_name"));
        user.setLastName(resultSet.getString("last_name"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));
        user.setRole(Role.valueOf(resultSet.getString("role")));
        user.setCreatedDate(ZonedDateTime.of(resultSet.getObject("created_date", LocalDateTime.class), ZoneId.systemDefault()));
        user.setUpdatedDate(ZonedDateTime.of(resultSet.getObject("updated_date", LocalDateTime.class), ZoneId.systemDefault()));
        user.setActive(resultSet.getBoolean("active"));
        user.setEmailUUID(resultSet.getString("email_uuid"));
        user.setAccountId(resultSet.getLong("account_id"));

        return user;
    }
}
