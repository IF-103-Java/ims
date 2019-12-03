package com.ita.if103java.ims.mapper.jdbc;

import com.ita.if103java.ims.entity.Transaction;
import com.ita.if103java.ims.entity.TransactionType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

@Component
public class TransactionRowMapper implements RowMapper<Transaction> {
    @Override
    public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
        final Transaction transaction = new Transaction();
        transaction.setId((BigInteger) rs.getObject("id"));
        transaction.setTimestamp(rs.getTimestamp("timestamp"));
        transaction.setAccountId(rs.getLong("account_id"));
        transaction.setWorkerId(rs.getLong("worker_id"));
        transaction.setItemId(rs.getLong("item_id"));
        transaction.setQuantity(rs.getLong("quantity"));
        transaction.setType(TransactionType.valueOf(rs.getString("type")));

        setValueOrNull(transaction::setAssociateId, rs.getLong("associate_id"), rs);
        setValueOrNull(transaction::setMovedFrom, rs.getLong("moved_from"), rs);
        setValueOrNull(transaction::setMovedTo, rs.getLong("moved_to"), rs);
        return transaction;
    }

    private <T> void setValueOrNull(Consumer<T> consumer, T value, ResultSet rs) throws SQLException {
        consumer.accept(rs.wasNull() ? null : value);
    }
}
