package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.entity.SavedItem;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class SavedItemRowMapper implements RowMapper<SavedItem> {
    @Override
    public SavedItem mapRow(ResultSet resultSet, int i) throws SQLException {
        SavedItem savedItem = new SavedItem();
        savedItem.setId(resultSet.getLong("id"));
        savedItem.setItemId(resultSet.getLong("item_id"));
        savedItem.setQuantity(resultSet.getInt("quantity"));
        savedItem.setWarehouseId(resultSet.getLong("warehouse_id"));
        return savedItem;
    }
}
