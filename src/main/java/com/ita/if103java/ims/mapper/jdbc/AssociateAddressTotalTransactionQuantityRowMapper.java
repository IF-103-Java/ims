package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.entity.AssociateAddressTotalTransactionQuantity;
import com.ita.if103java.ims.entity.AssociateType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.ita.if103java.ims.util.RowMapperUtil.setValueOrNull;

@Component
public class AssociateAddressTotalTransactionQuantityRowMapper implements RowMapper<AssociateAddressTotalTransactionQuantity> {

    @Override
    public AssociateAddressTotalTransactionQuantity mapRow(ResultSet rs, int i) throws SQLException {
        final AssociateAddressTotalTransactionQuantity entity = new AssociateAddressTotalTransactionQuantity();
        entity.setAssociateId(rs.getLong("associate_id"));
        entity.setAssociateName(rs.getString("associate_name"));
        entity.setAssociateType(AssociateType.valueOf(rs.getString("associate_type")));
        setValueOrNull(entity::setCountry, rs.getString("country"), rs);
        setValueOrNull(entity::setCity, rs.getString("city"), rs);
        setValueOrNull(entity::setAddress, rs.getString("address"), rs);
        setValueOrNull(entity::setLatitude, rs.getFloat("latitude"), rs);
        setValueOrNull(entity::setLongitude, rs.getFloat("longitude"), rs);
        entity.setTotalTransactionQuantity(rs.getLong("total_transaction_quantity"));
        return entity;
    }

}
