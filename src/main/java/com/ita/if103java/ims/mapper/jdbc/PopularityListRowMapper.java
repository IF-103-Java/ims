package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.dto.PopularityListDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class PopularityListRowMapper implements RowMapper<PopularityListDto> {
    @Override
    public PopularityListDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        PopularityListDto popularityListDto = new PopularityListDto();
        popularityListDto.setName(resultSet.getString("name"));
        popularityListDto.setQuantity(resultSet.getLong("quantity"));
        return popularityListDto;
    }
}
