package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.entity.Associate;
import com.ita.if103java.ims.entity.AssociateType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AssociateRowMapper implements RowMapper<Associate> {
    @Override
    public Associate mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Associate associate = new Associate();
        associate.setId(resultSet.getLong("id"));
        associate.setAccountId(resultSet.getLong("account_id"));
        associate.setName(resultSet.getString("name"));
        associate.setEmail(resultSet.getString("email"));
        associate.setPhone(resultSet.getString("phone"));
        associate.setAdditionalInfo(resultSet.getString("additional_info"));
        associate.setType(AssociateType.valueOf(resultSet.getString("type")));
        associate.setActive(resultSet.getBoolean("active"));

        return associate;
    }
}
