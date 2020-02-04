package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.BestAssociatesDao;
import com.ita.if103java.ims.entity.AssociateAddressTotalTransactionQuantity;
import com.ita.if103java.ims.entity.AssociateType;
import com.ita.if103java.ims.entity.TransactionType;
import com.ita.if103java.ims.exception.dao.CRUDException;
import com.ita.if103java.ims.mapper.jdbc.AssociateAddressTotalTransactionQuantityRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public class BestAssociatesDaoImpl implements BestAssociatesDao {

    private final JdbcTemplate jdbcTemplate;
    private final AssociateAddressTotalTransactionQuantityRowMapper mapper;

    @Autowired
    public BestAssociatesDaoImpl(JdbcTemplate jdbcTemplate, AssociateAddressTotalTransactionQuantityRowMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public List<AssociateAddressTotalTransactionQuantity> findByItem(Long accountId, Long itemId, Integer limit) {
        try {
            return jdbcTemplate.query(union(), mapper, getPreparedStatementArgs(accountId, itemId, limit));
        } catch (DataAccessException e) {
            throw new CRUDException("Error during WarehouseAdvisorDaoImpl.findBestAssociatesByItem", e);
        }
    }

    private String union() {
        final String q = Queries.SELECT_ALL_ASSOCIATES_GROUPED_BY_ITEM_SUM_BY_QUANTITY;
        return String.format("(%s) union (%s)", q, q);
    }

    private Object[] getPreparedStatementArgs(Long accountId, Long itemId, Integer limit) {
        return Stream.of(
            buildPartOfArgs(accountId, itemId, limit, TransactionType.IN, AssociateType.SUPPLIER),
            buildPartOfArgs(accountId, itemId, limit, TransactionType.OUT, AssociateType.CLIENT)
        ).flatMap(Stream::of).toArray();
    }

    private Object[] buildPartOfArgs(Long accountId, Long itemId, Integer limit,
                                     TransactionType transactionType, AssociateType associateType) {
        return new Object[]{itemId, transactionType.toString(), associateType.toString(), accountId, limit};
    }

    public static class Queries {
        public static final String SELECT_ALL_ASSOCIATES_GROUPED_BY_ITEM_SUM_BY_QUANTITY = """
                select a.id            as `associate_id`,
                       a.name          as `associate_name`,
                       a.type          as `associate_type`,
                       ad.country      as `country`,
                       ad.city         as `city`,
                       ad.address      as `address`,
                       ad.latitude     as `latitude`,
                       ad.longitude    as `longitude`,
                       sum(t.quantity) as `total_transaction_quantity`
                 from transactions t
                          join associates a on t.associate_id = a.id
                          join addresses ad on a.id = ad.associate_id
                 where t.item_id = ?
                   and t.type = ?
                   and a.type = ?
                   and t.account_id = ?
                   and a.active = TRUE
                 group by t.associate_id
                 order by sum(t.quantity) desc
                 limit ?
            """;
    }
}
