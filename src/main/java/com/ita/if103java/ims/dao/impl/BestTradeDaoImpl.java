package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.BestTradeDao;
import com.ita.if103java.ims.entity.TotalTransactionQuantity;
import com.ita.if103java.ims.entity.TransactionType;
import com.ita.if103java.ims.exception.CRUDException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BestTradeDaoImpl implements BestTradeDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public BestTradeDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<TotalTransactionQuantity> findBestClientsByItemId(Long itemId, Integer limit) {
        return findBestAssociatesByTransactionType(itemId, limit, TransactionType.OUT);
    }

    @Override
    public List<TotalTransactionQuantity> findBestSuppliersByItemId(Long itemId, Integer limit) {
        return findBestAssociatesByTransactionType(itemId, limit, TransactionType.IN);
    }

    @Override
    public List<TotalTransactionQuantity> findBestWarehousesByItemId(Long itemId, Integer limit) {
        try {
            return jdbcTemplate.query(
                Queries.SQL_SELECT_BEST_TOP_WAREHOUSES_BY_ITEM_ID,
                (rs, i) -> new TotalTransactionQuantity(
                    rs.getLong("top_warehouse__id"),
                    rs.getLong("total")
                ),
                itemId, limit
            );
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `select` best associates by item_id", e);
        }
    }

    private List<TotalTransactionQuantity> findBestAssociatesByTransactionType(Long itemId,
                                                                               Integer limit,
                                                                               TransactionType type) {
        try {
            return jdbcTemplate.query(
                Queries.SQL_SELECT_BEST_ASSOCIATES_BY_ITEM_ID,
                (rs, i) -> new TotalTransactionQuantity(
                    rs.getLong("associate_id"),
                    rs.getLong("total")
                ),
                itemId, type.toString(), limit
            );
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `select` best associates by item_id", e);
        }
    }

    public static final class Queries {
        public static final String SQL_SELECT_BEST_TOP_WAREHOUSES_BY_ITEM_ID = """
                select IFNULL(w1.top_warehouse_id, w2.top_warehouse_id) as `top_warehouse__id`,
                       sum(t.quantity)                                  as `total`
                from transactions t
                         left join warehouses w1 on t.moved_from = w1.id
                         left join warehouses w2 on t.moved_to = w2.id
                where t.item_id = ?
                  and t.type != 'MOVE'
                  and if(isnull(w1.top_warehouse_id), w2.active, w1.active) = TRUE
                group by `top_warehouse__id`
                order by sum(t.quantity) desc
                limit ?;
            """;

        public static final String SQL_SELECT_BEST_ASSOCIATES_BY_ITEM_ID = """
                select t.associate_id  as `associate_id`,
                       sum(t.quantity) as `total`
                from transactions t
                         join associates a on t.associate_id = a.id
                where t.item_id = ?
                  and t.type = ?
                  and a.active = TRUE
                group by t.associate_id
                order by sum(t.quantity) desc
                limit ?;
            """;
    }
}
