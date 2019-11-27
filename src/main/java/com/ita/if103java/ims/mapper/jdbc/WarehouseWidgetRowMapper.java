package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.dto.WarehouseWidgetDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class WarehouseWidgetRowMapper implements RowMapper<WarehouseWidgetDto> {
    @Override
    public WarehouseWidgetDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        WarehouseWidgetDto warehouseWidgetDto = new WarehouseWidgetDto();
        warehouseWidgetDto.setWarahouseCapacity(resultSet.getLong("warehouse_top_id"));
        warehouseWidgetDto.setWarehouseLoad(resultSet.getLong("warehouse_id"));
        return warehouseWidgetDto;
    }
}
