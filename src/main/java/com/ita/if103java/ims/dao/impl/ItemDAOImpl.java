package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.ItemDao;
import com.ita.if103java.ims.entity.Account;
import com.ita.if103java.ims.entity.Item;
import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.entity.Warehouse;
import com.ita.if103java.ims.mapper.jdbc.ItemRowMapper;
import com.ita.if103java.ims.mapper.jdbc.SavedItemRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
@Repository
public class ItemDAOImpl implements ItemDao {
    private JdbcTemplate jdbcTemplate;
    private ItemRowMapper itemRowMapper;
    private SavedItemRowMapper savedItemRowMapper;
    @Autowired
    public ItemDAOImpl(DataSource dataSource, ItemRowMapper itemRowMapper, SavedItemRowMapper savedItemRowMapper) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.itemRowMapper = itemRowMapper;
        this.savedItemRowMapper = savedItemRowMapper;
    }

    @Override
    public List<Item> getItems() {
        return jdbcTemplate.query("select * from Items", itemRowMapper);
    }

    @Override
    public List<SavedItem> getSavedItems() {
        return jdbcTemplate.query("select * from SavedItems", savedItemRowMapper);
    }

    @Override
    public Item findItemById(Long id) {
        return jdbcTemplate.queryForObject("select * from Items where id=?", itemRowMapper, id);
    }

    @Override
    public SavedItem findSavedItemById(Long id) {
        return jdbcTemplate.queryForObject("select * from SavedItems where id=?", savedItemRowMapper, id);
    }

    @Override
    public Item findItemByAccountId(Long id) {
        return jdbcTemplate.queryForObject("select * from Items where account_id=?", itemRowMapper);
    }

    @Override
    public SavedItem findSavedItemByItemId(Long id) {
       return jdbcTemplate.queryForObject("select * from SavedItems where item_id=?", savedItemRowMapper, id);
    }

    @Override
    public SavedItem findSavedItemByWarehouseId(Long id) {
        return jdbcTemplate.queryForObject("select * from SavedItems where warehouse_id=?", savedItemRowMapper, id);

    }

    @Override
    public void addItem(String name, String unit, String description, int volume, Long accountId, boolean active) {
jdbcTemplate.update("insert into Items(name_item, unit, description, volume, active, account_id) values(?, ?, ?, ?, ?, ?)", name, unit, description, volume, accountId, active);
    }

    @Override
    public void addSavedItem(Long itemId, int quantity, Long warehouseId) {
jdbcTemplate.update("insert into SavedItems(item_id, quantity, warehouse_id) values(?,?, ?)", itemId, quantity, warehouseId);
    }

    @Override
    public boolean updateSavedItem(Long warehouseId, Long savedItemId) {
        jdbcTemplate.update("update SavedItems set warehouse_id=? where id=?", warehouseId, savedItemId);
    return false;
    }

    @Override
    public boolean deleteItem(Long itemId) {
        jdbcTemplate.update("delete from Items where id=?", itemId);
        return false;
    }

    @Override
    public boolean deleteSavedItem(Long savedItemId) {
        jdbcTemplate.update("delete from SavedItems where id=?", savedItemId);
        return false;
    }
}
