package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.config.GeneratedKeyHolderFactory;
import com.ita.if103java.ims.dao.SavedItemDao;
import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.exception.dao.CRUDException;
import com.ita.if103java.ims.exception.dao.SavedItemNotFoundException;
import com.ita.if103java.ims.mapper.jdbc.SavedItemRowMapper;
import com.ita.if103java.ims.util.JDBCUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


@Repository
public class SavedItemDaoImpl implements SavedItemDao {
    private JdbcTemplate jdbcTemplate;
    private SavedItemRowMapper savedItemRowMapper;
    private GeneratedKeyHolderFactory generatedKeyHolderFactory;

    @Autowired
    public SavedItemDaoImpl(DataSource dataSource,
                            SavedItemRowMapper savedItemRowMapper,
                            GeneratedKeyHolderFactory generatedKeyHolderFactory) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.savedItemRowMapper = savedItemRowMapper;
        this.generatedKeyHolderFactory = generatedKeyHolderFactory;
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
    public List<SavedItem> findSavedItemByWarehouseId(Long id) {
        try {
            return jdbcTemplate.query(Queries.SQL_SELECT_SAVED_ITEMS_BY_WAREHOUSE_ID, savedItemRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new SavedItemNotFoundException("Failed to get savedItem during `select` {warehouse_id = " + id +
                "}", e);
        } catch (DataAccessException e) {
            throw new CRUDException("Failed during `select` {warehouse_id = " + id + "}", e);
        }

    }

    @Override
    public boolean existSavedItemByWarehouseId(Long id) {
        try {
            jdbcTemplate.query(Queries.SQL_SELECT_SAVED_ITEMS_BY_WAREHOUSE_ID, savedItemRowMapper, id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (DataAccessException e) {
            throw new CRUDException("Failed during `select` {warehouse_id = " + id + "}", e);
        }
    }

    @Override
    public SavedItem addSavedItem(SavedItem savedItem) {
        try {
            savedItem.setId(JDBCUtils.createWithAutogeneratedId(Long.class, (keyHolder) ->
                    jdbcTemplate.update(connection -> createSavedItemStatement(savedItem, connection), keyHolder),
                generatedKeyHolderFactory.newKeyHolder())
            );
            return savedItem;
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `insert` {item_id = " + savedItem.getItemId() + "warehouse_id" +
                savedItem.getWarehouseId() + "}", e);
        }

    }

    private PreparedStatement createSavedItemStatement(SavedItem savedItem, Connection connection) throws SQLException {
        int i = 0;
        PreparedStatement statement = connection.prepareStatement(Queries.SQL_INSERT_INTO_SAVED_ITEM,
            PreparedStatement.RETURN_GENERATED_KEYS);
        statement.setLong(++i, savedItem.getItemId());
        statement.setInt(++i, savedItem.getQuantity());
        statement.setLong(++i, savedItem.getWarehouseId());
        return statement;
    }

    @Override
    public boolean outComeSavedItem(SavedItem savedItem, int quantity) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_SET_QUANTITY_SAVED_ITEMS, quantity, savedItem.getId());

        } catch (DataAccessException e) {
            throw new CRUDException(
                "Error during `update` {quantity = " + savedItem.getQuantity() + "warehouse_id" + "}", e);

        }
        if (status == 0) {
            throw new SavedItemNotFoundException(
                "Failed to get savedItem during `update` {quantity = " + savedItem.getQuantity() + "warehouse_id" +
                    "}");
        }
        return true;
    }


    @Override
    public boolean updateSavedItem(Long warehouseId, Long savedItemId) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_SET_WAREHOUSE_ID_SAVED_ITEMS, warehouseId, savedItemId);

        } catch (DataAccessException e) {
            throw new CRUDException("Error during `update` {warehouse_id = " + warehouseId + " id " + savedItemId + "}"
                , e);

        }
        if (status == 0) {
            throw new SavedItemNotFoundException(
                "Failed to get savedItem during `update` {warehouse_id = " + warehouseId + "id" + savedItemId + "}");
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
            throw new SavedItemNotFoundException(
                "Failed to get soft delete savedItem during `delete` {id = " + savedItemId + "}");
        }
        return true;


    }

    @Override
    public Optional<SavedItem> findSavedItemByItemIdAndWarehouseId(Long itemId, Long warehouseId) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(Queries.SQL_SELECT_SAVED_ITEM_BY_ITEM_ID_AND_WAREHOUSE_ID,
                savedItemRowMapper, itemId, warehouseId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new CRUDException(
                "Failed during `select` {itemId = " + itemId + " warehouse_id = " + warehouseId + "}", e);
        }

    }

    @Override
    public void hardDelete(Long accountId) {
        try {
            jdbcTemplate.update(Queries.SQL_DELETE_SAVED_ITEM_BY_ID, accountId);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during hard `delete` saved item {accountId = " + accountId + "}", e);
        }
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
        static final String SQL_SELECT_SAVED_ITEM_BY_ITEM_ID_AND_WAREHOUSE_ID = """
                select *
                from saved_items
                where item_id = ? and warehouse_id = ?
            """;
        static final String SQL_DELETE_SAVED_ITEM_BY_ID = """
               DELETE
               FROM saved_items
               WHERE item_id IN
               (SELECT id
               FROM items
               WHERE account_id = ?);
            """;
    }
}
