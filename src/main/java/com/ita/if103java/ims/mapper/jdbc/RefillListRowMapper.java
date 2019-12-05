package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.dto.RefillListDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RefillListRowMapper implements RowMapper<RefillListDto> {
    @Override
    public RefillListDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        RefillListDto RefillListDto = new RefillListDto();
        RefillListDto.setName(resultSet.getString("name"));
        RefillListDto.setItem_name(resultSet.getString("item_name"));
        return RefillListDto;
    }
}
