package com.ita.if103java.ims.mapper;

import com.ita.if103java.ims.entity.Warehouse;
    import org.springframework.jdbc.core.RowMapper;
    import org.springframework.stereotype.Component;

    import java.sql.ResultSet;
    import java.sql.SQLException;

@Component
public class WarehouseRowMapper implements RowMapper<Warehouse> {
    @Override
    public Warehouse mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Warehouse Warehouse = new Warehouse();
        Warehouse.setId(resultSet.getLong("id"));
        Warehouse.setName(resultSet.getString("name"));
        Warehouse.setInfo(resultSet.getString("info"));
        Warehouse.setCapacity(resultSet.getInt("capacity"));
        Warehouse.setBottom(resultSet.getBoolean("isBottom"));
        Warehouse.setParentID(resultSet.getLong("parent"));
        Warehouse.setAccountID(resultSet.getLong("account_id"));
        Warehouse.setActive(resultSet.getBoolean("active"));

        return Warehouse;
    }
}
