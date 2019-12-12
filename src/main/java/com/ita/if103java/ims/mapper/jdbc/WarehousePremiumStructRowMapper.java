package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.dto.WarehousePremiumStructDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class WarehousePremiumStructRowMapper implements RowMapper<WarehousePremiumStructDto> {
    @Override
    public WarehousePremiumStructDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        WarehousePremiumStructDto warehousePremiumStructDto = new WarehousePremiumStructDto();
        warehousePremiumStructDto.setId(resultSet.getLong("id"));
        warehousePremiumStructDto.setName(resultSet.getString("name"));
        return warehousePremiumStructDto;
    }
}
