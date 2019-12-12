package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.entity.Address;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

@Component
public class AddressRowMapper implements RowMapper<Address> {
    @Override
    public Address mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        final Address address = new Address();
        address.setId(resultSet.getLong("id"));
        setValueOrNull(address::setCountry, resultSet.getString("country"), resultSet);
        setValueOrNull(address::setCity, resultSet.getString("city"), resultSet);
        setValueOrNull(address::setAddress, resultSet.getString("address"), resultSet);
        setValueOrNull(address::setZip, resultSet.getString("zip"), resultSet);
        setValueOrNull(address::setLongitude, resultSet.getFloat("longitude"), resultSet);
        setValueOrNull(address::setLatitude, resultSet.getFloat("latitude"), resultSet);
        setValueOrNull(address::setWarehouseId, resultSet.getLong("warehouse_id"), resultSet);
        setValueOrNull(address::setAssociateId, resultSet.getLong("associate_id"), resultSet);
        return address;
    }

    private <T> void setValueOrNull(Consumer<T> consumer, T value, ResultSet rs) throws SQLException {
        consumer.accept(rs.wasNull() ? null : value);
    }
}
