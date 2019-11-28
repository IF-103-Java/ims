package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.DashboardDao;
import com.ita.if103java.ims.dto.PopularityListDto;
import com.ita.if103java.ims.dto.RefillListDto;
import com.ita.if103java.ims.dto.WarehouseLoadDto;
import com.ita.if103java.ims.mapper.jdbc.PopularityListRowMapper;
import com.ita.if103java.ims.mapper.jdbc.RefillListRowMapper;
import com.ita.if103java.ims.mapper.jdbc.WarehouseLoadRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public class DashboardDaoImpl implements DashboardDao {
    private WarehouseLoadRowMapper warehouseLoadRowMapper;
    private PopularityListRowMapper popularityListRowMapper;
    private RefillListRowMapper refillListRowMapper;
    private JdbcTemplate jdbcTemplate;
    private static Logger logger = Logger.getLogger(DashboardDaoImpl.class.getName());

    @Autowired
    public DashboardDaoImpl(WarehouseLoadRowMapper warehouseLoadRowMapper, RefillListRowMapper refillListRowMapper, PopularityListRowMapper popularityListRowMapper, JdbcTemplate jdbcTemplate) {
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
              logger.log(Level.SEVERE, e.toString());
          }
        return null;
    }
    @Override
    public List<PopularityListDto> findPopularItems(){
        try {
            return jdbcTemplate.query(Queries.SQL_FIND_POPULARITY_WIDGET, popularityListRowMapper);
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, e.toString());
        }
        return null;
    }

    @Override
    public List<PopularityListDto> findUnpopularItems(){
        try {
            return jdbcTemplate.query(Queries.SQL_FIND_UNPOPULARITY_WIDGET, popularityListRowMapper);
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, e.toString());
        }
        return null;
    }

    @Override
    public List<RefillListDto> findEndedItems(){
        try {
            return jdbcTemplate.query(Queries.SQL_FIND_ENDED_ITEMS, refillListRowMapper );
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, e.toString());
        }
        return null;
    }

    class Queries {
        static final String SQL_FIND_POPULARITY_WIDGET =
            "SELECT it.name_item AS name, sum(ts.quantity) AS quantity " +
            "FROM transactions ts " +
            "JOIN items it " +
            "ON ts.item_id = it.id " +
            "WHERE (year(curdate())=year(timestamp)) " +
            "AND (month(curdate())=month(timestamp)) " +
            "AND type = 'OUT' " +
            "GROUP BY ts.item_id " +
            "ORDER BY sum(ts.quantity) DESC " +
            "LIMIT 3 ";

        static final String SQL_FIND_UNPOPULARITY_WIDGET =
            "SELECT it.name_item AS name, sum(ts.quantity) AS quantity " +
            "FROM transactions ts " +
            "JOIN items it " +
            "ON ts.item_id = it.id " +
            "WHERE (year(curdate())=year(timestamp)) " +
            "AND (month(curdate())=month(timestamp)) " +
            "AND type = 'OUT' " +
            "GROUP BY ts.item_id " +
            "ORDER BY sum(ts.quantity)" +
            "LIMIT 3 ";

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
            "WHERE si.quantity=0";
    }
}
