package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
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
        user.setRole((Role) resultSet.getObject("role"));
        user.setCreatedDate(resultSet.getObject("created_date", ZonedDateTime.class));
        user.setUpdatedDate(resultSet.getObject("updated_date", ZonedDateTime.class));
        user.setActive(resultSet.getBoolean("active"));
        user.setEmailUUID(resultSet.getString("email_uuid"));
        user.setAccountId(resultSet.getLong("account_id"));

        return user;
    }
}
