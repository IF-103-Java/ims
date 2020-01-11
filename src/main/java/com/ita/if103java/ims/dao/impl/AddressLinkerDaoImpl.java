package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.AddressLinkerDao;
import com.ita.if103java.ims.dto.AddressDto;
import com.ita.if103java.ims.dto.AssociateAddressDto;
import com.ita.if103java.ims.dto.WarehouseAddressDto;
import com.ita.if103java.ims.exception.dao.CRUDException;
import com.ita.if103java.ims.mapper.jdbc.AssociateAddressRowMapper;
import com.ita.if103java.ims.mapper.jdbc.WarehouseAddressRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AddressLinkerDaoImpl implements AddressLinkerDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final WarehouseAddressRowMapper warehouseAddressRowMapper;
    private final AssociateAddressRowMapper associateAddressRowMapper;

    @Autowired
    public AddressLinkerDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                WarehouseAddressRowMapper warehouseAddressRowMapper,
                                AssociateAddressRowMapper associateAddressRowMapper) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.associateAddressRowMapper = associateAddressRowMapper;
        this.warehouseAddressRowMapper = warehouseAddressRowMapper;
    }

    @Override
    public List<AssociateAddressDto> findAssociateAddressesByIds(List<Long> ids) {
        return findAddressByIds(Queries.SQL_FIND_ASSOCIATE_ADDRESSES_BY_IDS, ids, associateAddressRowMapper);
    }

    @Override
    public List<WarehouseAddressDto> findWarehouseAddressesByIds(List<Long> ids) {
        return findAddressByIds(Queries.SQL_FIND_WAREHOUSE_ADDRESSES_BY_IDS, ids, warehouseAddressRowMapper);
    }

    public <T extends AddressDto> List<T> findAddressByIds(String query, List<Long> ids, RowMapper<T> mapper) {
        try {
            final MapSqlParameterSource paramSource = new MapSqlParameterSource().addValue("ids", ids);
            return namedParameterJdbcTemplate.query(query, paramSource, mapper);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `select` address where id in " + ids, e);
        }
    }

    public static class Queries {
        public static final String SQL_FIND_ASSOCIATE_ADDRESSES_BY_IDS = """
            select a1.id,
                   a1.address,
                   a1.city,
                   a1.zip,
                   a1.country,
                   a1.latitude,
                   a1.longitude,
                   a1.associate_id,
                   a2.type as `associate_type`
            from addresses a1
                     join associates a2 on a1.associate_id = a2.id
            where a1.associate_id in (:ids)
            order by field(a1.associate_id, :ids)
            """;

        public static final String SQL_FIND_WAREHOUSE_ADDRESSES_BY_IDS = """
            select a.id,
                   a.address,
                   a.city,
                   a.zip,
                   a.country,
                   a.latitude,
                   a.longitude,
                   a.warehouse_id
            from addresses a
                     join warehouses w on a.warehouse_id = w.id
            where a.warehouse_id in (:ids)
            order by field(a.warehouse_id, :ids)
            """;
    }
}
