package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.entity.Account;
import com.ita.if103java.ims.entity.Item;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ItemRowMapper implements RowMapper<Item> {
    @Override
    public Item mapRow(ResultSet resultSet, int i) throws SQLException {
        Item item = new Item();
        item.setId(resultSet.getLong("id"));
        item.setName(resultSet.getString("name_item"));
        item.setUnit(resultSet.getString("unit"));
        item.setDescription(resultSet.getString("description"));
        item.setVolume(resultSet.getInt("volume"));
        item.setActive(resultSet.getBoolean("active"));
        Account account = new Account();
        account.setId(resultSet.getLong("account_id"));
        item.setAccount(account);
        return item;
    }
}
