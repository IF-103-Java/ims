package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.entity.TopWarehouseAddress;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.ita.if103java.ims.util.RowMapperUtil.setValueOrNull;

@Component
public class TopWarehouseAddressRowMapper implements RowMapper<TopWarehouseAddress> {

    @Override
    public TopWarehouseAddress mapRow(ResultSet rs, int i) throws SQLException {
        final TopWarehouseAddress entity = new TopWarehouseAddress();
        entity.setWarehouseId(rs.getLong("warehouse_id"));
        entity.setWarehouseName(rs.getString("warehouse_name"));
        setValueOrNull(entity::setCountry, rs.getString("country"), rs);
        setValueOrNull(entity::setCity, rs.getString("city"), rs);
        setValueOrNull(entity::setAddress, rs.getString("address"), rs);
        setValueOrNull(entity::setLatitude, rs.getFloat("latitude"), rs);
        setValueOrNull(entity::setLongitude, rs.getFloat("longitude"), rs);
        return entity;
    }

}
