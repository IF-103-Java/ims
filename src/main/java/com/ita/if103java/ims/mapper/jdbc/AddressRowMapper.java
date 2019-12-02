package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.entity.Address;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AddressRowMapper implements RowMapper<Address> {
    @Override
    public Address mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        final Address address = new Address(
            resultSet.getString("country"),
            resultSet.getString("city"),
            resultSet.getString("address"),
            resultSet.getString("zip"),
            resultSet.getFloat("latitude"),
            resultSet.getFloat("longitude")
        );
        address.setId(resultSet.getLong("id"));
        final long warehouseId = resultSet.getLong("warehouse_id");
        address.setWarehouseId(resultSet.wasNull() ? null : warehouseId);
        final long associateId = resultSet.getLong("associate_id");
        address.setAssociateId(resultSet.wasNull() ? null : associateId);
        return address;
    }
}
