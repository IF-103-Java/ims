package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.dto.EndingItemsDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EndingItemsRowMapper implements RowMapper<EndingItemsDto> {
    @Override
    public EndingItemsDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        EndingItemsDto EndingItemsDto = new EndingItemsDto();
        EndingItemsDto.setId(resultSet.getLong("id"));
        EndingItemsDto.setName(resultSet.getString("name"));
        EndingItemsDto.setItemName(resultSet.getString("name_item"));
        EndingItemsDto.setQuantity(resultSet.getInt("quantity"));
        return EndingItemsDto;
    }
}
