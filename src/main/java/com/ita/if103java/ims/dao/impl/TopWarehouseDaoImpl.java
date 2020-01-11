package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.TopWarehouseDao;
import com.ita.if103java.ims.exception.dao.CRUDException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TopWarehouseDaoImpl implements TopWarehouseDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TopWarehouseDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Long> findAllIds(Long accountId) {
        try {
            return jdbcTemplate.query(
                Queries.SQL_SELECT_ALL_TOP_WAREHOUSES_IDS,
                (rs, i) -> rs.getLong("id"),
                accountId
            );
        } catch (DataAccessException e) {
            throw new CRUDException("Error during finding all warehouses ids", e);
        }
    }

    public static class Queries {
        public static final String SQL_SELECT_ALL_TOP_WAREHOUSES_IDS = """
            select id
            from warehouses
            where parent_id is null
              and active = TRUE
              and account_id = ?
            """;
    }
}
