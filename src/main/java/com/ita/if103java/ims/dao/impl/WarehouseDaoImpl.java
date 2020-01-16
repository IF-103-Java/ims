package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.entity.Warehouse;
import com.ita.if103java.ims.exception.dao.CRUDException;
import com.ita.if103java.ims.exception.dao.WarehouseNotFoundException;
import com.ita.if103java.ims.mapper.jdbc.WarehouseRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class WarehouseDaoImpl implements WarehouseDao {
    private JdbcTemplate jdbcTemplate;
    private WarehouseRowMapper warehouseRowMapper;


    @Autowired
    public WarehouseDaoImpl(DataSource dataSource, WarehouseRowMapper warehouseRowMapper) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.warehouseRowMapper = warehouseRowMapper;
    }

    private PreparedStatement createWarehouseStatement(Warehouse warehouse, Connection connection) throws SQLException {
        int i = 0;
        PreparedStatement statement = connection.prepareStatement(Queries.SQL_CREATE_WAREHOUSE,
            PreparedStatement.RETURN_GENERATED_KEYS);
        statement.setString(++i, warehouse.getName());
        statement.setString(++i, warehouse.getInfo());
        statement.setInt(++i, warehouse.getCapacity());
        statement.setBoolean(++i, warehouse.isBottom());
        statement.setLong(++i, warehouse.getParentID());
        statement.setLong(++i, warehouse.getAccountID());
        statement.setLong(++i, warehouse.getTopWarehouseID());
        statement.setBoolean(++i, warehouse.isActive());

        return statement;
    }

    @Override
    public List<Warehouse> findAll(Pageable pageable, Long accountId) {
        try {
            return jdbcTemplate.query(Queries.SQL_SELECT_ALL_WAREHOUSES, warehouseRowMapper, accountId,
                pageable.getPageSize(), pageable.getOffset());

        } catch (DataAccessException e) {
            throw new WarehouseNotFoundException("Error during finding all warehouses", e);
        }
    }

    @Override
    public Map<Long, String> findAllWarehouseNames(Long accountId) {
        Map<Long, String> result = new HashMap<>();
        try {
            for (Map<String, Object> map : jdbcTemplate.queryForList(Queries.SQL_SELECT_ALL_NAMES, accountId)) {
                result.put(Long.valueOf(map.get("id").toString()), map.get("name").toString());
            }
        } catch (DataAccessException e) {
            throw new WarehouseNotFoundException("Error during `select * ` warehouses ", e);
        }
        return result;
    }

    @Override
    public Map<Long, String> findWarehouseNamesById(List<Long> idList) {
        String ids = idList.toString().substring(1, idList.toString().length() - 1);
        Map<Long, String> result = new HashMap<>();
        try {
            for (Map<String, Object> map : jdbcTemplate.queryForList(Queries.SQL_SELECT_NAMES_BY_ID, ids)) {
                result.put(Long.valueOf(map.get("id").toString()), map.get("name").toString());
            }
        } catch (DataAccessException e) {
            throw new WarehouseNotFoundException("Error during `select * ` warehouses ", e);
        }
        return result;
    }

    @Override
    public Warehouse findById(Long id, Long accountId) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_WAREHOUSE_BY_ID, warehouseRowMapper, id, accountId);
        } catch (EmptyResultDataAccessException e) {
            throw new WarehouseNotFoundException(e.getMessage());

        } catch (DataAccessException e) {
            throw new CRUDException("Error during finding warehouse by id = " + id, e);
        }

    }

    @Override
    public Warehouse create(Warehouse warehouse) {
        try {
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> createWarehouseStatement(warehouse, connection), holder);
            warehouse.setId(Optional.ofNullable(holder.getKey())
                .map(Number::longValue)
                .orElseThrow(() -> new CRUDException("Error during an warehouse creation: " +
                    "Autogenerated key is null")));

        } catch (DataAccessException e) {
            throw new CRUDException("create warehouse id = " + warehouse.getId(), e);
        }
        return warehouse;
    }


    @Override
    public Warehouse update(Warehouse warehouse) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_UPDATE_WAREHOUSE,
                warehouse.getName(), warehouse.getInfo(), warehouse.getCapacity(),
                warehouse.isBottom(), warehouse.getParentID(), warehouse.getTopWarehouseID(),
                warehouse.getAccountID(), warehouse.isActive(), warehouse.getId());
        } catch (DataAccessException e) {
            throw new CRUDException("Error duringupdate warehouse id = " + warehouse.getId(), e);
        }
        if (status == 0)
            throw new WarehouseNotFoundException("Failed to obtain warehouse during update warehouse {id = " + warehouse.getId());

        return warehouse;
    }

    @Override
    public boolean softDelete(Long id) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_SET_ACTIVE_STATUS_WAREHOUSE, false, id);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during soft `delete` warehouse {id = " + id + "}", e);
        }
        if (status == 0) {
            throw new WarehouseNotFoundException("Failed to obtain warehouse during soft `delete` {id = " + id + "}");
        }

        return true;
    }


    @Override
    public List<Warehouse> findByTopWarehouseID(Long id, Long accountId) {
        try {
            return jdbcTemplate.query(Queries.SQL_SELECT_BY_TOP_WAREHOUSE_ID, warehouseRowMapper, id, accountId);

        } catch (DataAccessException e) {
            throw new WarehouseNotFoundException("Error during finding all children of top-level-warehouse {Id = " + id + "}", e);
        }
    }

    @Override
    public Integer findLevelByParentID(Long id) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_LEVEL_WAREHOUSE_BY_PARENT_ID, Integer.class, id);

        } catch (DataAccessException e) {
            throw new WarehouseNotFoundException("Error during finding level of warehouse {Id = " + id + "}", e);
        }
    }

    @Override
    public Integer findQuantityOfWarehousesByAccountId(Long accountId) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_COUNT_QUANTITY_OF_WAREHOUSE_BY_ACCOUNT_ID, Integer.class, accountId);
        } catch (DataAccessException e) {
            throw new WarehouseNotFoundException("Error during finding warehouse quantity {account = " + accountId + "}", e);
        }
    }

    class Queries {

        static final String SQL_CREATE_WAREHOUSE = """
                INSERT INTO warehouses
                (name, info, capacity, is_bottom, parent_id, account_id, top_warehouse_id, active)
                VALUES(?,?,?,?,?,?,?,?);
            """;

        static final String SQL_SELECT_WAREHOUSE_BY_ID = """
                SELECT *
                FROM warehouses
                WHERE id = ? AND account_id = ?
            """;

        static final String SQL_SELECT_ALL_WAREHOUSES = """
                SELECT * FROM warehouses
                WHERE account_id = ?
                LIMIT ? OFFSET ?
            """;

        static final String SQL_UPDATE_WAREHOUSE = """
                UPDATE warehouses
                SET name= ?, info = ?, capacity = ?, is_bottom = ?, parent_id = ?,
                top_warehouse_id = ?, account_id = ?, active = ? WHERE id = ?
            """;

        static final String SQL_SELECT_BY_TOP_WAREHOUSE_ID = """
                SELECT *
                FROM warehouses
                WHERE top_warehouse_id = ? AND account_id = ?
            """;

        static final String SQL_SET_ACTIVE_STATUS_WAREHOUSE = """
                UPDATE warehouses
                SET active = ?
                WHERE id = ?
            """;

        static final String SQL_COUNT_QUANTITY_OF_WAREHOUSE_BY_ACCOUNT_ID = """
                SELECT COUNT(id)
                FROM warehouses
                WHERE parent_id IS NULL
                AND account_id = ?
            """;

        static final String SQL_LEVEL_WAREHOUSE_BY_PARENT_ID = """
                WITH RECURSIVE cte AS
                (SELECT id, 0 as depth
                 FROM warehouses
                 WHERE parent_id IS NULL
                 UNION ALL
                 SELECT w.id, cte.depth+1
                 FROM warehouses w JOIN cte
                 ON cte.id=w.parent_id)
                 SELECT depth
                 FROM cte
                 WHERE cte.id = ?
            """;

        static final String SQL_SELECT_ALL_NAMES = """
                SELECT id, name
                FROM warehouses
                WHERE account_id = ?
            """;

        static final String SQL_SELECT_NAMES_BY_ID = """
                SELECT id, name
                FROM warehouses
                WHERE id IN (?)
            """;
    }
}
