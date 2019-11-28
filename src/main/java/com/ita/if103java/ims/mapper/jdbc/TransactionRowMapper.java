package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.entity.Transaction;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class TransactionRowMapper implements RowMapper<Transaction> {
    @Override
    public Transaction mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        final Transaction transaction = new Transaction(
            resultSet.getLong("account_id"),
            resultSet.getLong("associate_id"),
            resultSet.getLong("item_id"),
            resultSet.getLong("quantity"),
            resultSet.getLong("moved_from"),
            resultSet.getLong("moved_to"),
            Transaction.Type.valueOf(resultSet.getString("type"))
        );
        transaction.setId(new BigInteger(Integer.valueOf(resultSet.getInt("id")).toString()));
        transaction.setTimestamp(resultSet.getTimestamp("timestamp"));
        return transaction;
    }
}
