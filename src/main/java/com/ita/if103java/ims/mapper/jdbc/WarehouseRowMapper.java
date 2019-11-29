package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.entity.Warehouse;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class WarehouseRowMapper implements RowMapper<Warehouse> {
    @Override
    public Warehouse mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Warehouse warehouse = new Warehouse();

        warehouse.setId(resultSet.getLong("id"));
        warehouse.setName(resultSet.getString("name"));
        warehouse.setInfo(resultSet.getString("info"));
        warehouse.setCapacity(resultSet.getInt("capacity"));
        warehouse.setBottom(resultSet.getBoolean("isBottom"));
        warehouse.setParentID(resultSet.getLong("parent"));
        warehouse.setAccountID(resultSet.getLong("account_id"));
        warehouse.setTopWarehouseID(resultSet.getLong("top_warehouse_id"));
        warehouse.setActive(resultSet.getBoolean("active"));

        return warehouse;
    }
}
