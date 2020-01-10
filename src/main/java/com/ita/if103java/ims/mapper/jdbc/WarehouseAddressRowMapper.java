package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.dto.WarehouseAddressDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.ita.if103java.ims.util.RowMapperUtil.setValueOrNull;

@Component
public class WarehouseAddressRowMapper implements RowMapper<WarehouseAddressDto> {
    @Override
    public WarehouseAddressDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        final WarehouseAddressDto address = new WarehouseAddressDto();
        address.setId(rs.getLong("id"));
        setValueOrNull(address::setCountry, rs.getString("country"), rs);
        setValueOrNull(address::setCity, rs.getString("city"), rs);
        setValueOrNull(address::setAddress, rs.getString("address"), rs);
        setValueOrNull(address::setZip, rs.getString("zip"), rs);
        setValueOrNull(address::setLongitude, rs.getFloat("longitude"), rs);
        setValueOrNull(address::setLatitude, rs.getFloat("latitude"), rs);
        setValueOrNull(address::setWarehouseId, rs.getLong("warehouse_id"), rs);
        return address;
    }
}
