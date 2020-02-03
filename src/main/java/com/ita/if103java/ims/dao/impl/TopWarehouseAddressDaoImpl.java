package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.TopWarehouseAddressDao;
import com.ita.if103java.ims.entity.TopWarehouseAddress;
import com.ita.if103java.ims.exception.dao.CRUDException;
import com.ita.if103java.ims.mapper.jdbc.TopWarehouseAddressRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TopWarehouseAddressDaoImpl implements TopWarehouseAddressDao {
    private final JdbcTemplate jdbcTemplate;
    private final TopWarehouseAddressRowMapper mapper;

    @Autowired
    public TopWarehouseAddressDaoImpl(JdbcTemplate jdbcTemplate,
                                      TopWarehouseAddressRowMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public List<TopWarehouseAddress> findAll(Long accountId) {
        try {
            return jdbcTemplate.query(Queries.SQL_SELECT_ALL_TOP_WAREHOUSE_ADDRESSES, mapper, accountId);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during finding all warehouses ids", e);
        }
    }

    public static class Queries {
        public static final String SQL_SELECT_ALL_TOP_WAREHOUSE_ADDRESSES = """
                select w.id        as `warehouse_id`,
                       w.name      as `warehouse_name`,
                       a.country   as `country`,
                       a.city      as `city`,
                       a.address   as `address`,
                       a.latitude  as `latitude`,
                       a.longitude as `longitude`
                from warehouses w
                         join addresses a on w.id = a.warehouse_id
                where w.parent_id is null
                  and w.active = TRUE
                  and w.account_id = ?
            """;
    }
}
