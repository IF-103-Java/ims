package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.dto.PopularItemsDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class PopularityListRowMapper implements RowMapper<PopularItemsDto> {
    @Override
    public PopularItemsDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        PopularItemsDto popularItemsDto = new PopularItemsDto();
        popularItemsDto.setName(resultSet.getString("name"));
        popularItemsDto.setQuantity(resultSet.getLong("quantity"));
        return popularItemsDto;
    }
}
