package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.ItemDao;
import com.ita.if103java.ims.entity.Item;
import com.ita.if103java.ims.exception.CRUDException;
import com.ita.if103java.ims.exception.EntityNotFoundException;
import com.ita.if103java.ims.mapper.jdbc.ItemRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ItemDaoImpl implements ItemDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemDaoImpl.class);
    private JdbcTemplate jdbcTemplate;
    private ItemRowMapper itemRowMapper;

    @Autowired
    public ItemDaoImpl(DataSource dataSource, ItemRowMapper itemRowMapper) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.itemRowMapper = itemRowMapper;
    }

    @Override
    public List<Item> getItems() {
        try {
            return jdbcTemplate.query(Queries.SQL_SELECT_ITEMS, itemRowMapper);
        } catch (DataAccessException e) {
            throw crudException(e.toString(), "getItem", "*");
        }
    }


    @Override
    public Item findItemByName(String name) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_ITEM_BY_NAME, itemRowMapper, name);
        } catch (EmptyResultDataAccessException e) {
            throw itemEntityNotFoundException(e.getMessage(), "name_item = " + name);
        } catch (DataAccessException e) {
            throw crudException(e.toString(), "get", "name_item  = " + name);
        }

    }

    @Override
    public Item findItemByAccountId(Long id) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_ITEM_BY_ACCOUNT_ID, itemRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw itemEntityNotFoundException(e.getMessage(), "account_Id = " + id);
        } catch (DataAccessException e) {
            throw crudException(e.toString(), "get", "account_Id  = " + id);
        }

    }

    @Override
    public Item findItemById(Long id) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_ITEM_BY_ID, itemRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw itemEntityNotFoundException(e.getMessage(), "id = " + id);
        } catch (DataAccessException e) {
            throw crudException(e.toString(), "get", "id  = " + id);
        }
    }

    @Override
    public Item addItem(Item item) {
        try {
            jdbcTemplate.update(Queries.SQL_INSERT_INTO_ITEM, item.getName(), item.getUnit(), item.getDescription(), item.getVolume(), item.isActive(), item.getAccountId());
            return item;
        } catch (DataAccessException e) {
            throw crudException(e.toString(), "add", "account_id = " + item.getAccountId());
        }
    }

    @Override
    public boolean softDeleteItem(String name) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_SET_ACTIVE_STATUS_ITEM, false, name);

        } catch (DataAccessException e) {
            throw crudException(e.toString(), "SoftDelete", "name = " + name);
        }
        if (status == 0) {
            throw itemEntityNotFoundException("SoftDelete item exception", "name = " + name);
        }
        return true;
    }

    private EntityNotFoundException itemEntityNotFoundException(String message, String attribute) {
        EntityNotFoundException exception = new EntityNotFoundException(message);
        LOGGER.error("EntityNotFoundException exception. Item is not found ({}). Message: {}", attribute, message);
        return exception;
    }

    private CRUDException crudException(String message, String operation, String attribute) {
        CRUDException exception = new CRUDException(message);
        LOGGER.error("CRUDException exception. Operation:({}) Item ({}) exception. Message: {}", operation, attribute, message);
        return exception;
    }

    class Queries {
        static final String SQL_SELECT_ITEMS = "select * from Items";
        static final String SQL_SELECT_ITEM_BY_NAME = "select * from Items where name_item=?";
        static final String SQL_SELECT_ITEM_BY_ACCOUNT_ID = "select * from Items where account_id=?";
        static final String SQL_SELECT_ITEM_BY_ID = "select * from Items where id=?";
        static final String SQL_INSERT_INTO_ITEM = "insert into Items(name_item, unit, description, volume, active, account_id) values(?, ?, ?, ?, ?, ?)";
        static final String SQL_SET_ACTIVE_STATUS_ITEM = "update Items set active= ? where name_item=?";

    }
}
