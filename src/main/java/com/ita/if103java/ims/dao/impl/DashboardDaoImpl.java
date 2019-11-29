package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.DashboardDao;
import com.ita.if103java.ims.dto.PopularityListDto;
import com.ita.if103java.ims.dto.RefillListDto;
import com.ita.if103java.ims.dto.WarehouseLoadDto;
import com.ita.if103java.ims.exception.CRUDException;
import com.ita.if103java.ims.mapper.jdbc.PopularityListRowMapper;
import com.ita.if103java.ims.mapper.jdbc.RefillListRowMapper;
import com.ita.if103java.ims.mapper.jdbc.WarehouseLoadRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public class DashboardDaoImpl implements DashboardDao {
    private final static Logger LOGGER = LoggerFactory.getLogger(DashboardDaoImpl.class);
    private WarehouseLoadRowMapper warehouseLoadRowMapper;
    private PopularityListRowMapper popularityListRowMapper;
    private RefillListRowMapper refillListRowMapper;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DashboardDaoImpl(WarehouseLoadRowMapper warehouseLoadRowMapper,
                            RefillListRowMapper refillListRowMapper,
                            PopularityListRowMapper popularityListRowMapper,
                            JdbcTemplate jdbcTemplate) {
        this.refillListRowMapper = refillListRowMapper;
        this.popularityListRowMapper = popularityListRowMapper;
        this.warehouseLoadRowMapper = warehouseLoadRowMapper;
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<WarehouseLoadDto> findWarehouseWidgets() {
        try {
            return jdbcTemplate.query(Queries.SQL_FIND_WAREHOUSE_WIDGETS, warehouseLoadRowMapper);
        }catch (DataAccessException e) {
            throw crudException("WarehouseWidget",e.toString());
        }
    }
    @Override
    public List<PopularityListDto> findPopularItems(int quantity){
        try {
            return jdbcTemplate.query(Queries.SQL_FIND_POPULARITY_WIDGET + Queries.SQL_ATR_POP, popularityListRowMapper, quantity);
        } catch (DataAccessException e) {
            throw crudException("PopularItems", e.toString());
        }
    }

    @Override
    public List<PopularityListDto> findUnpopularItems(int quantity){
        try {
            return jdbcTemplate.query(Queries.SQL_FIND_POPULARITY_WIDGET + Queries.SQL_ATR_UNPOP, popularityListRowMapper, quantity);
        } catch (DataAccessException e) {
            throw crudException("UnpopularItems",e.toString());
        }
    }

    @Override
    public List<RefillListDto> findEndedItems(Long min_quantity){
        try {
            return jdbcTemplate.query(Queries.SQL_FIND_ENDED_ITEMS, refillListRowMapper, min_quantity);
        } catch (DataAccessException e) {
            throw crudException("EndedItems",e.toString());
        }
    }

    private CRUDException crudException( String attribute, String message) {
        CRUDException exception = new CRUDException(message);
        LOGGER.error("CRUDException exception. Dashboard ({}) exception. Message: {}", attribute, message);
        return exception;
    }

    class Queries {
        static final String SQL_FIND_POPULARITY_WIDGET =
            "SELECT it.name_item AS name, sum(ts.quantity) AS quantity " +
            "FROM transactions ts " +
            "JOIN items it " +
            "ON ts.item_id = it.id " +
            "WHERE type = 'OUT' " +
            "GROUP BY ts.item_id ";

        static final String SQL_ATR_POP =
            "ORDER BY sum(ts.quantity) DESC " +
            "LIMIT ?";
        static final String SQL_ATR_UNPOP =
            "ORDER BY sum(ts.quantity)" +
            "LIMIT ? ";

        static final String SQL_FIND_WAREHOUSE_WIDGETS =
            "SELECT wh.top_warehouse_id, sum(wh.capacity) AS capacity, sum(quantity*volume) AS charge " +
            "FROM saved_items si " +
            "JOIN items it " +
            "ON si.item_id=it.id " +
            "JOIN warehouses wh " +
            "ON si.warehouse_id=wh.id " +
            "WHERE wh.is_bottom=1 " +
            "GROUP BY wh.top_warehouse_id";
        static final String SQL_FIND_ENDED_ITEMS =
            "SELECT wh.name, it.name_item AS item_name " +
            "FROM saved_items si " +
            "JOIN warehouses wh " +
            "ON si.warehouse_id = wh.id " +
            "JOIN items it " +
            "ON si.item_id = it.id " +
            "WHERE si.quantity < ?";
    }
}
