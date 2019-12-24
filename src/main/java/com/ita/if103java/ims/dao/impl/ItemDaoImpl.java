package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.ItemDao;
import com.ita.if103java.ims.entity.Item;
import com.ita.if103java.ims.exception.CRUDException;
import com.ita.if103java.ims.exception.ItemNotFoundException;
import com.ita.if103java.ims.mapper.jdbc.ItemRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ItemDaoImpl implements ItemDao {
    private JdbcTemplate jdbcTemplate;
    private ItemRowMapper itemRowMapper;

    @Autowired
    public ItemDaoImpl(DataSource dataSource, ItemRowMapper itemRowMapper) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.itemRowMapper = itemRowMapper;
    }

    @Override
    public List<Item> getItems(String sort, int size, long offset) {
        try {

      final String query = String.format("""
          select * from items order by %s limit %s offset %s
        """, sort, size, offset);
            return jdbcTemplate.query(query, itemRowMapper);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `select * `", e);
        }
    }


    @Override
    public Item findItemByName(String name) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_ITEM_BY_NAME, itemRowMapper, name);
        } catch (EmptyResultDataAccessException e) {
            throw new ItemNotFoundException("Failed to get item during `select` {name = " + name + "}", e);
        } catch (DataAccessException e) {
            throw new CRUDException("Failed during `select` {name = " + name + "}", e);
        }

    }

    @Override
    public Item findItemByAccountId(Long id) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_ITEM_BY_ACCOUNT_ID, itemRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new ItemNotFoundException("Failed to get item during `select` {account_id = " + id + "}", e);
        } catch (DataAccessException e) {
            throw new CRUDException("Failed during `select` {account_id = " + id + "}", e);
        }

    }

    @Override
    public Item findItemById(Long id) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_ITEM_BY_ID, itemRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ItemNotFoundException("Failed to get item during `select` {id = " + id + "}", e);
        } catch (DataAccessException e) {
            throw new CRUDException("Failed during `select` {id = " + id + "}", e);
        }
    }

    @Override
    public Item addItem(Item item) {
        try {
            jdbcTemplate.update(Queries.SQL_INSERT_INTO_ITEM, item.getName(), item.getUnit(), item.getDescription(),
                item.getVolume(), item.isActive(), item.getAccountId());
            return item;
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `insert` {account_id = " + item.getAccountId() + "}", e);
        }
    }

    @Override
    public boolean softDeleteItem(Long id) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_SET_ACTIVE_STATUS_ITEM, false, id);

        } catch (DataAccessException e) {
            throw new CRUDException("Error during soft `delete` {name = " + id + "}", e);

        }
        if (status == 0) {
            throw new ItemNotFoundException("Failed to get soft delete item during `delete` {name = " + id + "}");
        }
        return true;
    }

    class Queries {
        static final String SQL_SELECT_ITEM_BY_NAME = """
            select *
            from items
            where name_item=?
        """;
        static final String SQL_SELECT_ITEM_BY_ACCOUNT_ID = """
            select *
            from items
            where account_id=?
        """;
        static final String SQL_SELECT_ITEM_BY_ID = """
            select *
            from items
            where id=?
        """;
        static final String SQL_INSERT_INTO_ITEM = """
            insert into items(name_item, unit, description, volume, active, account_id)
            values(?, ?, ?, ?, ?, ?)
        """;
        static final String SQL_SET_ACTIVE_STATUS_ITEM = """
            update items
            set active= ?
            where id=?
        """;
    }
}
