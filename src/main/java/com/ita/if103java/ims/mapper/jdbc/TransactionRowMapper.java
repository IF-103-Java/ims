package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.entity.Transaction;
import com.ita.if103java.ims.entity.TransactionType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class TransactionRowMapper implements RowMapper<Transaction> {
    @Override
    public Transaction mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        final Transaction transaction = new Transaction();
        transaction.setId(new BigInteger(Long.valueOf(resultSet.getLong("id")).toString()));
        transaction.setTimestamp(resultSet.getTimestamp("timestamp"));

        transaction.setAccountId(resultSet.getLong("account_id"));
        transaction.setItemId(resultSet.getLong("item_id"));
        transaction.setQuantity(resultSet.getLong("quantity"));
        transaction.setType(TransactionType.valueOf(resultSet.getString("type")));

        final long associateId = resultSet.getLong("associate_id");
        transaction.setAssociateId(resultSet.wasNull() ? null : associateId);
        final long movedFrom = resultSet.getLong("moved_from");
        transaction.setMovedFrom(resultSet.wasNull() ? null : movedFrom);
        final long movedTo = resultSet.getLong("moved_to");
        transaction.setMovedTo(resultSet.wasNull() ? null : movedTo);
        return transaction;
    }
}
