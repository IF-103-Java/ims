package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.TransactionDao;
import com.ita.if103java.ims.dto.ItemTransactionRequestDto;
import com.ita.if103java.ims.entity.Transaction;
import com.ita.if103java.ims.entity.TransactionType;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.exception.dao.CRUDException;
import com.ita.if103java.ims.exception.dao.TransactionNotFoundException;
import com.ita.if103java.ims.mapper.jdbc.TransactionRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;

import static com.ita.if103java.ims.util.JDBCUtils.createWithAutogeneratedId;


@Repository
public class TransactionDaoImpl implements TransactionDao {
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedJdbcTemplate;
    private TransactionRowMapper mapper;

    @Autowired
    public TransactionDaoImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate,
                              TransactionRowMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public Transaction create(Transaction transaction) {
        try {
            final Long id = createWithAutogeneratedId(keyHolder ->
                namedJdbcTemplate.update(Queries.SQL_CREATE_TRANSACTION, getSqlParameterSource(transaction), keyHolder)
            );
            transaction.setId(id);
            return transaction;
        } catch (DataAccessException e) {
            throw new CRUDException("Error during a transaction 'insert' -> Transaction.create(" + transaction + ")", e);
        }
    }

    @Override
    public Transaction create(ItemTransactionRequestDto itemTransactionRequestDto,
                              User user,
                              Long associateId,
                              TransactionType type) {
        final Transaction transaction = new Transaction();
        transaction.setAccountId(user.getAccountId());
        transaction.setItemId(itemTransactionRequestDto.getItemId());
        transaction.setQuantity(itemTransactionRequestDto.getQuantity());
        transaction.setWorkerId(user.getId());
        transaction.setType(type);
        switch (type) {
            case OUT -> {
                transaction.setMovedFrom(itemTransactionRequestDto.getSourceWarehouseId());
                transaction.setAssociateId(associateId);
            }
            case IN -> {
                transaction.setMovedTo(itemTransactionRequestDto.getDestinationWarehouseId());
                transaction.setAssociateId(associateId);
            }
            case MOVE -> {
                transaction.setMovedFrom(itemTransactionRequestDto.getSourceWarehouseId());
                transaction.setMovedTo(itemTransactionRequestDto.getDestinationWarehouseId());
            }
        }
        return transaction;
    }

    @Override
    public Transaction findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_TRANSACTION_BY_ID, mapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new TransactionNotFoundException("Transaction not found -> Transaction.findById(" + id + ")", e);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during a transaction 'select' -> Transaction.findById(" + id + ")", e);
        }
    }

    @Override
    public Transaction findByIdAndAccountId(Long id, Long accountId) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_TRANSACTION_BY_ID_AND_ACCOUNT_ID, mapper, id, accountId);
        } catch (EmptyResultDataAccessException e) {
            throw new TransactionNotFoundException("Transaction not found -> " +
                "Transaction.findByIdAndAccountId(" + id + ", " + accountId + ")", e);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during a transaction 'select' -> " +
                "Transaction.findByIdAndAccountId(" + id + ", " + accountId + ")", e);
        }
    }

    @Override
    public void hardDelete(Long accountId) {
        try {
            jdbcTemplate.update(Queries.SQL_DELETE_TRANSACTION_BY_ID, accountId);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during hard `delete` transaction {accountId = " + accountId + "}", e);
        }
    }

    private MapSqlParameterSource getSqlParameterSource(Transaction transaction) {
        final MapSqlParameterSource parameterSource = new MapSqlParameterSource()
            .addValue("account_id", transaction.getAccountId())
            .addValue("worker_id", transaction.getWorkerId())
            .addValue("item_id", transaction.getItemId())
            .addValue("quantity", transaction.getAccountId())
            .addValue("associate_id", transaction.getAssociateId())
            .addValue("moved_from", transaction.getMovedFrom())
            .addValue("moved_to", transaction.getMovedTo())
            .addValue("type", transaction.getType());
        parameterSource.registerSqlType("type", Types.VARCHAR);
        return parameterSource;
    }

    public static final class Queries {
        public static final String SQL_CREATE_TRANSACTION = """
                insert into transactions(account_id, worker_id, associate_id, item_id, quantity, moved_from, moved_to, type)
                values (:account_id, :worker_id, :associate_id, :item_id, :quantity, :moved_from, :moved_to, :type)
            """;

        public static final String SQL_SELECT_TRANSACTION_BY_ID = """
                select *
                from transactions
                where id = ?
            """;

        public static final String SQL_SELECT_TRANSACTION_BY_ID_AND_ACCOUNT_ID = """
                select *
                from transactions
                where id = ? and account_id = ?
            """;

        public static final String SQL_DELETE_TRANSACTION_BY_ID = """
                DELETE
                FROM transactions
                WHERE account_id = ?
            """;
    }
}
