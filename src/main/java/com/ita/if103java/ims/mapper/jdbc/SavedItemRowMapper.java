package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.entity.Item;
import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.entity.Warehouse;
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
        Item item = new Item();
        item.setId(resultSet.getLong("item_id"));
        savedItem.setItem(item);
        savedItem.setQuantity(resultSet.getInt("quantity"));
        Warehouse warehouse = new Warehouse();
        warehouse.setId(resultSet.getLong("warehouse_id"));
        savedItem.setWarehouse(warehouse);
        return savedItem;
    }
}
