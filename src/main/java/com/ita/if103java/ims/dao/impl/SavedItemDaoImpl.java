package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.SavedItemDao;
import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.exception.CRUDException;
import com.ita.if103java.ims.exception.SavedItemNotFoundException;
import com.ita.if103java.ims.mapper.jdbc.SavedItemRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class SavedItemDaoImpl implements SavedItemDao {
    private JdbcTemplate jdbcTemplate;
    private SavedItemRowMapper savedItemRowMapper;

    @Autowired
    public SavedItemDaoImpl(DataSource dataSource, SavedItemRowMapper savedItemRowMapper) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.savedItemRowMapper = savedItemRowMapper;
    }

    @Override
    public List<SavedItem> getSavedItems() {
        try {
            return jdbcTemplate.query(Queries.SQL_SELECT_SAVED_ITEMS, savedItemRowMapper);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `select * `", e);
        }

    }


    @Override
    public SavedItem findSavedItemById(Long id) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_SAVED_ITEMS_BY_ID, savedItemRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new SavedItemNotFoundException("Failed to get savedItem during `select` {id = " + id + "}", e);
        } catch (DataAccessException e) {
            throw new CRUDException("Failed during `select` {id = " + id + "}", e);

        }

    }

    @Override
    public List<SavedItem> findSavedItemByItemId(Long id) {
        try {
            return jdbcTemplate.query(Queries.SQL_SELECT_SAVED_ITEMS_BY_ITEM_ID, savedItemRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new SavedItemNotFoundException("Failed to get savedItem during `select` {item_id = " + id + "}", e);
        } catch (DataAccessException e) {
            throw new CRUDException("Failed during `select` {item_id = " + id + "}", e);

        }

    }

    @Override
    public SavedItem findSavedItemByWarehouseId(Long id) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_SAVED_ITEMS_BY_WAREHOUSE_ID, savedItemRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new SavedItemNotFoundException("Failed to get savedItem during `select` {warehouse_id = " + id + "}", e);
        } catch (DataAccessException e) {
            throw new CRUDException("Failed during `select` {warehouse_id = " + id + "}", e);
        }

    }

    @Override
    public SavedItem addSavedItem(SavedItem savedItem) {
        try {
            jdbcTemplate.update(Queries.SQL_INSERT_INTO_SAVED_ITEM, savedItem.getItemId(), savedItem.getQuantity(), savedItem.getWarehouseId());
            return savedItem;
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `insert` {item_id = " + savedItem.getItemId() + "warehouse_id" + savedItem.getWarehouseId() + "}", e);
        }

    }

    @Override
    public boolean outComeSavedItem(SavedItem savedItem, int quantity) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_SET_QUANTITY_SAVED_ITEMS, quantity, savedItem.getId());

        } catch (DataAccessException e) {
            throw new CRUDException("Error during `update` {quantity = " + savedItem.getQuantity() + "warehouse_id" + "}", e);

        }
        if (status == 0) {
            throw new SavedItemNotFoundException("Failed to get savedItem during `update` {quantity = " + savedItem.getQuantity() + "warehouse_id" + "}");
        }
        return true;
    }


    @Override
    public boolean updateSavedItem(Long warehouseId, Long savedItemId) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_SET_WAREHOUSE_ID_SAVED_ITEMS, warehouseId, savedItemId);

        } catch (DataAccessException e) {
            throw new CRUDException("Error during `update` {warehouse_id = " + warehouseId + "id" + savedItemId + "}", e);

        }
        if (status == 0) {
            throw new SavedItemNotFoundException("Failed to get savedItem during `update` {warehouse_id = " + warehouseId + "id" + savedItemId + "}");
        }
        return true;

    }

    @Override
    public boolean deleteSavedItem(Long savedItemId) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_DELETE_SAVED_ITEM_BY_SAVED_ITEM_ID, savedItemId);

        } catch (DataAccessException e) {
            throw new CRUDException("Error during  `delete` {id = " + savedItemId + "}", e);

        }
        if (status == 0) {
            throw new SavedItemNotFoundException("Failed to get soft delete savedItem during `delete` {id = " + savedItemId + "}");
        }
        return true;


    }

    class Queries {
        static final String SQL_SELECT_SAVED_ITEMS = """
            select *
            from saved_items
        """;
        static final String SQL_SELECT_SAVED_ITEMS_BY_ID = """
            select *
            from saved_items
            where id=?
        """;
        static final String SQL_SELECT_SAVED_ITEMS_BY_ITEM_ID = """
            select *
            from saved_items
            where item_id=?
        """;
        static final String SQL_SELECT_SAVED_ITEMS_BY_WAREHOUSE_ID = """
            select *
            from saved_items
            where warehouse_id=?
        """;
        static final String SQL_INSERT_INTO_SAVED_ITEM = """
            insert into saved_items(item_id, quantity, warehouse_id)
            values(?,?, ?)
        """;
        static final String SQL_DELETE_SAVED_ITEM_BY_SAVED_ITEM_ID = """
            delete from saved_items
            where id=?
        """;
        static final String SQL_SET_WAREHOUSE_ID_SAVED_ITEMS = """
            update saved_items
            set warehouse_id=?
            where id=?
        """;
        static final String SQL_SET_QUANTITY_SAVED_ITEMS = """
            update saved_items
            set quantity=?
            where id=?
        """;
    }
}
