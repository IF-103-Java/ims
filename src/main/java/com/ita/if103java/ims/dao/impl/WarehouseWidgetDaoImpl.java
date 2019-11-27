package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.WarehouseWidgetDao;
import com.ita.if103java.ims.dto.WarehouseWidgetDto;
import com.ita.if103java.ims.mapper.jdbc.WarehouseWidgetRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public class WarehouseWidgetDaoImpl implements WarehouseWidgetDao {
    private WarehouseWidgetRowMapper warehouseWidgetRowMapper;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public WarehouseWidgetDaoImpl(WarehouseWidgetRowMapper warehouseWidgetRowMapper, JdbcTemplate jdbcTemplate) {
        this.warehouseWidgetRowMapper = warehouseWidgetRowMapper;
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<WarehouseWidgetDto> findWarehouseWidget() {
          try {
               return jdbcTemplate.query(Queries.SQL_FIND_WAREHOUSE_WIDGETS, warehouseWidgetRowMapper);
           } catch (DataAccessException e) {
              System.out.println(e.getStackTrace());
          }
        return null;
    }

    class Queries {
        static final String SQL_FIND_WAREHOUSE_WIDGETS =
            "SELECT sum(wh.capacity), sum(quantity*volume)\n" +
            "FROM saved_items si\n" +
            "JOIN items it\n" +
            "ON si.item_id=it.id\n" +
            "JOIN warehouses wh\n" +
            "ON si.warehouse_id=wh.id\n" +
            "WHERE wh.is_bottom=1\n" +
            "GROUP BY wh.top_warehouse_id";
    }
}
