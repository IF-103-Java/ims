package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.AddressDao;
import com.ita.if103java.ims.entity.Address;
import com.ita.if103java.ims.exception.AddressNotFoundException;
import com.ita.if103java.ims.exception.CRUDException;
import com.ita.if103java.ims.mapper.jdbc.AddressRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AddressDaoImpl implements AddressDao {
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedJdbcTemplate;
    private AddressRowMapper mapper;

    @Autowired
    public AddressDaoImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate,
                          AddressRowMapper addressRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.mapper = addressRowMapper;
    }

    @Override
    public Address createWarehouseAddress(Long warehouseId, Address address) {
        final Long addressId = createAddress(
            Queries.SQL_CREATE_NEW_WAREHOUSE_ADDRESS,
            getSqlParameterSource(address).addValue("warehouse_id", warehouseId),
            "Error during `create` warehouseId=" + warehouseId
        );
        address.setId(addressId);
        return address;
    }

    @Override
    public Address createAssociateAddress(Long associateId, Address address) {
        final Long addressId = createAddress(
            Queries.SQL_CREATE_NEW_ASSOCIATE_ADDRESS,
            getSqlParameterSource(address).addValue("associate_id", associateId),
            "Error during address `create` associateId=" + associateId
        );
        address.setId(addressId);
        return address;
    }

    @Override
    public Address updateWarehouseAddress(Long warehouseId, Address address) {
        updateAddress(
            Queries.SQL_UPDATE_WAREHOUSE_ADDRESS,
            getSqlParameterSource(address).addValue("warehouse_id", warehouseId),
            "Failed to `update` address warehouseId=" + warehouseId,
            "Error during `update` address warehouseId=" + warehouseId
        );
        return address;
    }

    @Override
    public Address updateAssociateAddress(Long associateId, Address address) {
        updateAddress(
            Queries.SQL_UPDATE_ASSOCIATE_ADDRESS,
            getSqlParameterSource(address).addValue("associate_id", associateId),
            "Failed to `update` address associateId=" + associateId,
            "Error during `update` address associateId=" + associateId
        );
        return address;
    }

    @Override
    public Address findById(Long addressId) {
        return findAddressById(
            Queries.SQL_SELECT_ADDRESS_BY_ID,
            addressId,
            "Failed to obtain address during `select` addressId=" + addressId,
            "Error during `select` addressId=" + addressId
        );
    }

    @Override
    public Address findByWarehouseId(Long warehouseId) {
        return findAddressById(
            Queries.SQL_SELECT_WAREHOUSE_ADDRESS,
            warehouseId,
            "Failed to obtain address during `select` warehouseId=" + warehouseId,
            "Error during `select` warehouseId=" + warehouseId
        );
    }

    @Override
    public Address findByAssociateId(Long associateId) {
        return findAddressById(
            Queries.SQL_SELECT_ASSOCIATE_ADDRESS,
            associateId,
            "Failed to obtain address during `select` associateId=" + associateId,
            "Error during `select` associateId=" + associateId
        );
    }

    private Long createAddress(String query, MapSqlParameterSource sqlParameterSource,
                               String crudErrorMessage) {
        try {
            final KeyHolder keyHolder = new GeneratedKeyHolder();
            namedJdbcTemplate.update(query, sqlParameterSource, keyHolder);
            return Optional
                .ofNullable(keyHolder.getKey())
                .map(Number::longValue)
                .orElseThrow(() -> new CRUDException(
                    "Failed to generate PK -> Address.create(" + sqlParameterSource.getValues() + ")"
                ));
        } catch (DataAccessException e) {
            throw new CRUDException(crudErrorMessage, e);
        }
    }

    public void updateAddress(String query, MapSqlParameterSource sqlParameterSource,
                              String notFoundMessage, String crudErrorMessage) {
        try {
            final int rowsAffected = namedJdbcTemplate.update(query, sqlParameterSource);
            if (rowsAffected == 0) {
                throw new AddressNotFoundException(notFoundMessage);
            }
        } catch (DataAccessException e) {
            throw new CRUDException(crudErrorMessage, e);
        }
    }

    public Address findAddressById(String query, Long id, String notFoundMessage, String crudErrorMessage) {
        try {
            return jdbcTemplate.queryForObject(query, mapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new AddressNotFoundException(notFoundMessage, e);
        } catch (DataAccessException e) {
            throw new CRUDException(crudErrorMessage, e);
        }
    }

    private MapSqlParameterSource getSqlParameterSource(Address address) {
        return new MapSqlParameterSource()
            .addValue("country", address.getCountry())
            .addValue("city", address.getCity())
            .addValue("address", address.getAddress())
            .addValue("zip", address.getZip())
            .addValue("latitude", address.getLatitude())
            .addValue("longitude", address.getLongitude());
    }

    public static class Queries {
        public static final String SQL_CREATE_NEW_WAREHOUSE_ADDRESS = """
                insert into addresses(warehouse_id, country, city, address, zip, latitude, longitude)
                values (:warehouse_id, :country, :city, :address, :zip, :latitude, :longitude)
            """;

        public static final String SQL_CREATE_NEW_ASSOCIATE_ADDRESS = """
                insert into addresses(associate_id, country, city, address, zip, latitude, longitude)
                values (:associate_id, :country, :city, :address, :zip, :latitude, :longitude)
            """;

        public static final String SQL_UPDATE_WAREHOUSE_ADDRESS = """
                update addresses
                set country   = :country,
                    city      = :city,
                    address   = :address,
                    zip       = :zip,
                    latitude  = :latitude,
                    longitude = :longitude
                where warehouse_id = :warehouse_id
            """;

        public static final String SQL_UPDATE_ASSOCIATE_ADDRESS = """
                update addresses
                set country   = :country,
                    city      = :city,
                    address   = :address,
                    zip       = :zip,
                    latitude  = :latitude,
                    longitude = :longitude
                where associate_id = :associate_id
            """;

        public static final String SQL_SELECT_ADDRESS_BY_ID = """
                select *
                from addresses
                where id = ?
            """;

        public static final String SQL_SELECT_WAREHOUSE_ADDRESS = """
                select *
                from addresses
                where warehouse_id = ?
            """;

        public static final String SQL_SELECT_ASSOCIATE_ADDRESS = """
                select *
                from addresses
                where associate_id = ?
            """;
    }
}