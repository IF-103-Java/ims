package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.entity.ChargeCapacity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ChargeCapacityRowMapper implements RowMapper<ChargeCapacity> {
    @Override
    public ChargeCapacity mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        ChargeCapacity chargeCapacity = new ChargeCapacity();
        chargeCapacity.setCapacity(resultSet.getLong("capacity"));
        chargeCapacity.setCharge(resultSet.getLong("charge"));
        return chargeCapacity;
    }
}
