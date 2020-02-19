package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.config.GeneratedKeyHolderFactory;
import com.ita.if103java.ims.dao.ItemDao;
import com.ita.if103java.ims.entity.Item;
import com.ita.if103java.ims.exception.dao.CRUDException;
import com.ita.if103java.ims.exception.dao.ItemNotFoundException;
import com.ita.if103java.ims.exception.dao.SavedItemNotFoundException;
import com.ita.if103java.ims.mapper.jdbc.ItemRowMapper;
import com.ita.if103java.ims.util.JDBCUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.ita.if103java.ims.util.JDBCUtils.getOrder;


@Repository
public class ItemDaoImpl implements ItemDao {
    private JdbcTemplate jdbcTemplate;
    private ItemRowMapper itemRowMapper;
    private GeneratedKeyHolderFactory generatedKeyHolderFactory;

    @Autowired
    public ItemDaoImpl(DataSource dataSource,
                       ItemRowMapper itemRowMapper,
                       GeneratedKeyHolderFactory generatedKeyHolderFactory) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.itemRowMapper = itemRowMapper;
        this.generatedKeyHolderFactory = generatedKeyHolderFactory;
    }

    @Override
    public List<Item> getItems(long accountId, int size, long offset, Sort sort) {
        try {
            if (sort.isSorted()) {
                return jdbcTemplate.query(String.format(Queries.SQL_SELECT_PAGINATED_SORTED_ITEMS, getOrder(sort)),
                    itemRowMapper, accountId, size, offset);
            }
            return jdbcTemplate.query(Queries.SQL_SELECT_PAGINATED_ITEMS, itemRowMapper, accountId, size, offset);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `select * `", e);
        }
    }

    @Override
    public Integer countItemsById(long accountId) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_COUNT_ITEM_BY_ACCOUNT_ID, Integer.class, accountId);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `select * `", e);
        }
    }


    @Override
    public Item findItemByName(String name, Long accountId) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_ITEM_BY_NAME, itemRowMapper, accountId, name);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new CRUDException("Failed during `select` {name = " + name + "}", e);
        }

    }

    @Override
    public List<Item> findItemByAccountId(Long id) {
        try {
            return jdbcTemplate.query(Queries.SQL_SELECT_ITEM_BY_ACCOUNT_ID, itemRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ItemNotFoundException("Failed to get item during `select` {account_id = " + id + "}", e);
        } catch (DataAccessException e) {
            throw new CRUDException("Failed during `select` {account_id = " + id + "}", e);
        }

    }


    @Override
    public Item findItemById(Long id, Long accountId) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_ITEM_BY_ID_AND_ACCOUNT_ID, itemRowMapper, accountId,
                id);
        } catch (EmptyResultDataAccessException e) {
            throw new ItemNotFoundException("Failed to get item during `select` {id = " + id + "}", e);
        } catch (DataAccessException e) {
            throw new CRUDException("Failed during `select` {id = " + id + "}", e);
        }
    }

    @Override
    public List<Item> findItemsById(String id, Long accountId) {
        try {
            return jdbcTemplate
                .query(String.format(Queries.SQL_SELECT_ITEMS_BY_ID_AND_ACCOUNT_ID, id), itemRowMapper, accountId);
        } catch (EmptyResultDataAccessException e) {
            throw new ItemNotFoundException("Failed to get item during `select` {id = " + id + "}", e);
        } catch (DataAccessException e) {
            throw new CRUDException("Failed during `select` {id = " + id + "}", e);
        }
    }

    @Override
    public boolean isExistItemById(Long id, Long accountId) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_IF_EXIST_ITEM_BY_ACCOUNT_ID,
                (row, rs) -> row.getBoolean("active"), accountId, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ItemNotFoundException("Failed to get item during `select` {id = " + id + "}", e);
        } catch (DataAccessException e) {
            throw new CRUDException("Failed during `select` {id = " + id + "}", e);
        }
    }


    @Override
    public Item addItem(Item item) {
        try {
            item.setActive(true);
            item.setId(JDBCUtils.createWithAutogeneratedId(Long.class, (keyHolder) ->
                    jdbcTemplate.update(connection -> createItemStatement(item, connection), keyHolder),
                generatedKeyHolderFactory.newKeyHolder())
            );
            return item;
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `insert` {account_id = " + item.getAccountId() + "}", e);
        }
    }

    private PreparedStatement createItemStatement(Item item, Connection connection) throws SQLException {
        int i = 0;
        PreparedStatement statement = connection.prepareStatement(Queries.SQL_INSERT_INTO_ITEM,
            PreparedStatement.RETURN_GENERATED_KEYS);
        statement.setString(++i, item.getName());
        statement.setString(++i, item.getUnit());
        statement.setString(++i, item.getDescription());
        statement.setInt(++i, item.getVolume());
        statement.setBoolean(++i, item.isActive());
        statement.setObject(++i, item.getAccountId());
        return statement;
    }

    @Override
    public boolean softDeleteItem(Long id, Long accountId) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_SET_ACTIVE_STATUS_ITEM, false, accountId, id);

        } catch (DataAccessException e) {
            throw new CRUDException("Error during soft `delete` {name = " + id + "}", e);

        }
        if (status == 0) {
            throw new ItemNotFoundException("Failed to get soft delete item during `delete` {name = " + id + "}");
        }
        return true;
    }

    @Override
    public void hardDelete(Long accountId) {
        try {
            jdbcTemplate.update(Queries.SQL_DELETE_ITEM_BY_ACCOUNT_ID, accountId);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during hard `delete` item {accountId = " + accountId + "}", e);
        }
    }

    @Override
    public List<Item> findItemsByNameQuery(String query, long accountId) {
        try {
            return jdbcTemplate.query(Queries.SQL_SELECT_ITEM_BY_QUERY_AND_ACCOUNT_ID, itemRowMapper,
                "%" + query.toLowerCase() + "%", accountId);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `select * `", e);
        }
    }

    @Override
    public Item updateItem(Item item) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_UPDATE_ITEM, item.getName(), item.getUnit(),
                item.getDescription(), item.getVolume(), item.getAccountId(), item.getId());
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `update` {id " + item.getId() + "}", e);
        }
        if (status == 0) {
            throw new ItemNotFoundException("Failed to get savedItem during `update` {id" + item.getId() + "}");
        }
        return item;
    }

    class Queries {
        static final String SQL_SELECT_PAGINATED_ITEMS = """
            select *
            from items
            where account_id=?
            limit ? offset ?
            """;
        static final String SQL_SELECT_PAGINATED_SORTED_ITEMS = """
            select *
            from items
            where account_id=? and active = true
            order by %s, id
            limit ? offset ?
            """;
        static final String SQL_SELECT_COUNT_ITEM_BY_ACCOUNT_ID = """
            select count(*)
            from items
            where account_id=?
            """;
        static final String SQL_SELECT_ITEM_BY_NAME = """
                select *
                from items
                where account_id=? and name_item=?
            """;
        static final String SQL_SELECT_ITEM_BY_ACCOUNT_ID = """
                select *
                from items
                where account_id=?
            """;
        static final String SQL_SELECT_IF_EXIST_ITEM_BY_ACCOUNT_ID = """
                select active
                from items
                where account_id=? and id=?
            """;
        static final String SQL_SELECT_ITEMS = """
                select id
                 from items
                 where account_id=? and id IN (%s);
            """;
        static final String SQL_SELECT_ITEM_BY_ID_AND_ACCOUNT_ID = """
                select *
                from items
                where account_id=? and id=?
            """;
        static final String SQL_SELECT_ITEMS_BY_ID_AND_ACCOUNT_ID = """
                select *
                from items
                where account_id=? and id IN (%s)
            """;
        static final String SQL_INSERT_INTO_ITEM = """
                insert into items(name_item, unit, description, volume, active, account_id)
                values(?, ?, ?, ?, ?, ?)
            """;
        static final String SQL_SET_ACTIVE_STATUS_ITEM = """
                update items
                set active= ?
                where account_id=? and id=?
            """;
        static final String SQL_SELECT_ITEM_BY_QUERY_AND_ACCOUNT_ID = """
                select *
                from items
                where lower(name_item) like ? and account_id=?
            """;
        static final String SQL_UPDATE_ITEM = """
                update items
                set name_item= ?, unit = ?, description = ?, volume = ?
                where account_id = ? and id = ?
            """;

        static final String SQL_DELETE_ITEM_BY_ACCOUNT_ID = """
                DELETE
                FROM items
                WHERE account_id = ?
            """;
    }
}

