package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.dto.WarehouseLoadDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class WarehousePremiumLoadRowMapper implements RowMapper<WarehouseLoadDto> {
    @Override
    public WarehouseLoadDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        WarehouseLoadDto warehouseLoadDto = new WarehouseLoadDto();
        warehouseLoadDto.setId(resultSet.getLong("id"));
        warehouseLoadDto.setCapacity(resultSet.getLong("capacity"));
        warehouseLoadDto.setCharge(resultSet.getLong("charge"));
        return warehouseLoadDto;
    }
}
