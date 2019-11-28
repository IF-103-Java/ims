package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.TransactionDao;
import com.ita.if103java.ims.entity.Transaction;
import com.ita.if103java.ims.mapper.jdbc.TransactionRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class TransactionDaoImpl implements TransactionDao {
    private static Logger LOGGER = LoggerFactory.getLogger(TransactionDaoImpl.class);
    private TransactionRowMapper mapper;
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public TransactionDaoImpl(DataSource dataSource, TransactionRowMapper mapper) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.mapper = mapper;
    }

    @Override
    public Transaction create(Transaction transaction) throws DataAccessException {
        return withErrorLogging(() -> {
            final KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> getPreparedStatement(connection, transaction), keyHolder);
            transaction.setId((BigInteger) keyHolder.getKey());
            return transaction;
        }, "Transaction creation failure => " + transaction);
    }

    @Override
    public Transaction findById(BigInteger id) throws DataAccessException {
        return withErrorLogging(() -> jdbcTemplate.queryForObject(Queries.SQL_SELECT_TRANSACTION_BY_ID, mapper, id),
            "Transaction error on 'select by id' => id=" + id);
    }

    @Override
    public List<Transaction> findAll(Integer offset, Integer limit) throws DataAccessException {
        return withErrorLogging(() -> jdbcTemplate.query(Queries.SQL_SELECT_ALL_TRANSACTIONS, mapper, limit, offset),
            "Transaction error on 'select *'");
    }

    @Override
    public List<Transaction> findAll(Map<String, Object> params,
                                     Integer offset, Integer limit,
                                     String orderBy) throws DataAccessException, IllegalStateException {
        return withErrorLogging(() -> {
                final String where = Stream
                    .of("account_id", "associate_id", "item_id", "quantity", "moved_from", "moved_to", "type")
                    .filter(params::containsKey)
                    .map(x -> String.format("%s %s :%s", x, params.get(x) == null ? "is" : "=", x))
                    .collect(Collectors.joining("\n and "));

                if (where.isBlank())
                    throw new IllegalStateException("Invalid or empty filter parameters " + params);

                final String query = String.format("""
                        select *
                        from transactions
                        where %s
                        order by %s
                        limit %s offset %s""",
                    where,
                    orderBy.startsWith("-") ? orderBy.substring(1) + " desc" : orderBy,
                    limit, offset
                );
                return namedParameterJdbcTemplate.query(query, params, mapper);
            },
            "Transaction error on 'select * with filters'");
    }

    private <T> T withErrorLogging(Supplier<T> supplier, String message) throws DataAccessException {
        try {
            return supplier.get();
        } catch (DataAccessException e) {
            LOGGER.error(message, e);
            throw e;
        }
    }

    private PreparedStatement getPreparedStatement(Connection connection, Transaction transaction) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(Queries.SQL_CREATE_TRANSACTION, Statement.RETURN_GENERATED_KEYS);
        int i = 0;
        ps.setLong(++i, transaction.getAccountId());
        safeSetIdOrNull(ps, ++i, transaction.getAssociateId());
        ps.setLong(++i, transaction.getItemId());
        ps.setLong(++i, transaction.getQuantity());
        safeSetIdOrNull(ps, ++i, transaction.getMovedFrom());
        safeSetIdOrNull(ps, ++i, transaction.getMovedTo());
        ps.setString(++i, transaction.getType().toString());
        return ps;
    }

    public void safeSetIdOrNull(PreparedStatement ps, int index, Long value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.BIGINT);
        } else {
            ps.setLong(index, value);
        }
    }

    public static final class Queries {
        public static final String SQL_CREATE_TRANSACTION = """
                insert into transactions(account_id, associate_id, item_id, quantity, moved_from, moved_to, type)
                values (?, ?, ?, ?, ?, ?, ?)
            """;

        public static final String SQL_SELECT_TRANSACTION_BY_ID = """
                select * from transactions where id = ?
            """;


        public static final String SQL_SELECT_ALL_TRANSACTIONS = """
                select * from transactions limit ? offset ?
            """;
    }
}
