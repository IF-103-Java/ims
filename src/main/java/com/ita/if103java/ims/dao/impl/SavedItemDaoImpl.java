package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.SavedItemDao;
import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.exception.CRUDException;
import com.ita.if103java.ims.exception.EntityNotFoundException;
import com.ita.if103java.ims.mapper.jdbc.SavedItemRowMapper;
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
public class SavedItemDaoImpl implements SavedItemDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemDaoImpl.class);
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
            throw crudException(e.toString(), "getSavedItem", "*");
        }

    }

    @Override
    public SavedItem findSavedItemById(Long id) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_SAVED_ITEMS_BY_ID, savedItemRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw savedItemEntityNotFoundException(e.getMessage(), "id = " + id);
        } catch (DataAccessException e) {
            throw crudException(e.toString(), "get", "id  = " + id);
        }

    }

    @Override
    public SavedItem findSavedItemByItemId(Long id) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_SAVED_ITEMS_BY_ITEM_ID, savedItemRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw savedItemEntityNotFoundException(e.getMessage(), "item_id = " + id);
        } catch (DataAccessException e) {
            throw crudException(e.toString(), "get", "item_id  = " + id);
        }

    }

    @Override
    public SavedItem findSavedItemByWarehouseId(Long id) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_SAVED_ITEMS_BY_WAREHOUSE_ID, savedItemRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw savedItemEntityNotFoundException(e.getMessage(), "warehouse_id = " + id);
        } catch (DataAccessException e) {
            throw crudException(e.toString(), "get", "warehouse_id  = " + id);
        }

    }

    @Override
    public SavedItem addSavedItem(SavedItem savedItem) {
        try {
            jdbcTemplate.update(Queries.SQL_INSERT_INTO_SAVED_ITEM, savedItem.getItemId(), savedItem.getQuantity(), savedItem.getWarehouseId());
            return savedItem;
        } catch (DataAccessException e) {
            throw crudException(e.toString(), "add", "item_id = " + savedItem.getItemId() + " warehouse_id = " + savedItem.getWarehouseId());
        }

    }

    @Override
    public boolean outComeSavedItem(SavedItem savedItem, int quantity) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_SET_QUANTITY_SAVED_ITEMS, quantity, savedItem.getId());

        } catch (DataAccessException e) {
            throw crudException(e.toString(), "Update", "quantity = " + quantity);
        }
        if (status == 0) {
            throw savedItemEntityNotFoundException("Update savedItem exception", "quantity = " + quantity);
        }
        return true;
    }


    @Override
    public boolean updateSavedItem(Long warehouseId, Long savedItemId) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_SET_WAREHOUSE_ID_SAVED_ITEMS, warehouseId, savedItemId);

        } catch (DataAccessException e) {
            throw crudException(e.toString(), "Update", "warehouse_id = " + warehouseId + " id = " + savedItemId);
        }
        if (status == 0) {
            throw savedItemEntityNotFoundException("Update savedItem exception", "warehouse_id = " + warehouseId + " id = " + savedItemId);
        }
        return true;

    }

    @Override
    public boolean deleteSavedItem(Long savedItemId) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_DELETE_SAVED_ITEM_BY_SAVED_ITEM_ID, savedItemId);

        } catch (DataAccessException e) {
            throw crudException(e.toString(), "Delete", "id = " + savedItemId);
        }
        if (status == 0) {
            throw savedItemEntityNotFoundException("Delete savedItem exception", "id = " + savedItemId);
        }
        return true;


    }

    private EntityNotFoundException savedItemEntityNotFoundException(String message, String attribute) {
        EntityNotFoundException exception = new EntityNotFoundException(message);
        LOGGER.error("EntityNotFoundException exception. SavedItem is not found ({}). Message: {}", attribute, message);
        return exception;
    }

    private CRUDException crudException(String message, String operation, String attribute) {
        CRUDException exception = new CRUDException(message);
        LOGGER.error("CRUDException exception. Operation:({}) SavedItem ({}) exception. Message: {}", operation, attribute, message);
        return exception;
    }

    class Queries {
        static final String SQL_SELECT_SAVED_ITEMS = "select * from SavedItems";
        static final String SQL_SELECT_SAVED_ITEMS_BY_ID = "select * from SavedItems where id=?";
        static final String SQL_SELECT_SAVED_ITEMS_BY_ITEM_ID = "select * from SavedItems where item_id=?";
        static final String SQL_SELECT_SAVED_ITEMS_BY_WAREHOUSE_ID = "select * from SavedItems where warehouse_id=?";
        static final String SQL_INSERT_INTO_SAVED_ITEM = "insert into SavedItems(item_id, quantity, warehouse_id) values(?,?, ?)";
        static final String SQL_DELETE_SAVED_ITEM_BY_SAVED_ITEM_ID = "delete from SavedItems where id=?";
        static final String SQL_SET_WAREHOUSE_ID_SAVED_ITEMS = "update SavedItems set warehouse_id=? where id=?";
        static final String SQL_SET_QUANTITY_SAVED_ITEMS = "update SavedItems set quantity=? where id=?";
    }
}
