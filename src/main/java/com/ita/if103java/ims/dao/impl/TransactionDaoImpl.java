package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.TransactionDao;
import com.ita.if103java.ims.entity.Transaction;
import com.ita.if103java.ims.exception.CRUDException;
import com.ita.if103java.ims.exception.TransactionNotFoundException;
import com.ita.if103java.ims.mapper.jdbc.TransactionRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
            final KeyHolder keyHolder = new GeneratedKeyHolder();
            namedJdbcTemplate.update(Queries.SQL_CREATE_TRANSACTION, getSqlParameterSource(transaction), keyHolder);
            final BigInteger id = Optional
                .ofNullable((BigInteger) keyHolder.getKey())
                .orElseThrow(() -> new CRUDException("Failed to generate PK -> Transaction.create(" + transaction + ")"));
            transaction.setId(id);
            return transaction;
        } catch (DataAccessException e) {
            throw new CRUDException("Error during a transaction 'insert' -> Transaction.create(" + transaction + ")", e);
        }
    }

    @Override
    public Transaction findById(BigInteger id) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_TRANSACTION_BY_ID, mapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new TransactionNotFoundException("Transaction not found -> Transaction.findById(" + id + ")", e);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during a transaction 'select' -> Transaction.findById(" + id + ")", e);
        }
    }

    @Override
    public List<Transaction> findAll(Map<String, ?> params,
                                     Integer offset, Integer limit,
                                     String orderBy) {
        final String where = Stream
            .of("account_id", "worker_id", "associate_id", "item_id", "quantity", "moved_from", "moved_to", "type")
            .filter(params::containsKey)
            .map(x -> buildSqlFilterCondition(x, params.get(x)))
            .collect(Collectors.joining("\n and "));

        final String query = String.format("""
                    select *
                    from transactions
                    where %s
                    order by %s
                    limit %s offset %s
                """,
            where.isBlank() ? "TRUE" : where,
            orderBy.startsWith("-") ? orderBy.substring(1) + " desc" : orderBy,
            limit, offset
        );
        try {
            final MapSqlParameterSource paramSource = new MapSqlParameterSource(params);
            paramSource.registerSqlType("type", Types.VARCHAR);
            return namedJdbcTemplate.query(query, paramSource, mapper);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during a transactions 'select' -> " +
                "Transaction.findAll(" + params + ", " + offset + ", " + limit + ", " + orderBy + ")", e);
        }
    }

    private String buildSqlFilterCondition(String columnName, Object columnValue) {
        // associate_id = :associate_id
        // associate_id is :associate_id (if :associate_id ==  null)
        return String.format("%s %s :%s", columnName, columnValue == null ? "is" : "=", columnName);
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
                select * from transactions where id = ?
            """;
    }
}
