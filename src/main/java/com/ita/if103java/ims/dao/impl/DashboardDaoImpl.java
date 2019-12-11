package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.DashboardDao;
import com.ita.if103java.ims.dto.PopularItemsRequestDto;
import com.ita.if103java.ims.dto.PopularItemsDto;
import com.ita.if103java.ims.dto.EndingItemsDto;
import com.ita.if103java.ims.dto.WarehouseLoadDto;
import com.ita.if103java.ims.dto.WarehousePremiumStructDto;
import com.ita.if103java.ims.entity.ChargeCapacity;
import com.ita.if103java.ims.exception.CRUDException;
import com.ita.if103java.ims.mapper.jdbc.ChargeCapacityRowMapper;
import com.ita.if103java.ims.mapper.jdbc.PopularItemsRowMapper;
import com.ita.if103java.ims.mapper.jdbc.EndingItemsRowMapper;
import com.ita.if103java.ims.mapper.jdbc.WarehouseLoadRowMapper;
import com.ita.if103java.ims.mapper.jdbc.WarehousePremiumLoadRowMapper;
import com.ita.if103java.ims.mapper.jdbc.WarehousePremiumStructRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

import static com.ita.if103java.ims.entity.PopType.TOP;

@Repository
public class DashboardDaoImpl implements DashboardDao {
    private final static Logger LOGGER = LoggerFactory.getLogger(DashboardDaoImpl.class);
    private WarehouseLoadRowMapper warehouseLoadRowMapper;
    private PopularItemsRowMapper popularItemsRowMapper;
    private EndingItemsRowMapper endingItemsRowMapper;
    private WarehousePremiumStructRowMapper warehousePremiumStructRowMapper;
    private WarehousePremiumLoadRowMapper warehousePremiumLoadRowMapper;
    private ChargeCapacityRowMapper chargeCapacityRowMapper;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DashboardDaoImpl(WarehouseLoadRowMapper warehouseLoadRowMapper,
                            EndingItemsRowMapper endingItemsRowMapper,
                            PopularItemsRowMapper popularItemsRowMapper,
                            WarehousePremiumStructRowMapper warehousePremiumStructRowMapper,
                            WarehousePremiumLoadRowMapper warehousePremiumLoadRowMapper,
                            ChargeCapacityRowMapper chargeCapacityRowMapper,
                            JdbcTemplate jdbcTemplate) {
        this.warehousePremiumStructRowMapper = warehousePremiumStructRowMapper;
        this.endingItemsRowMapper = endingItemsRowMapper;
        this.popularItemsRowMapper = popularItemsRowMapper;
        this.warehouseLoadRowMapper = warehouseLoadRowMapper;
        this.warehousePremiumLoadRowMapper = warehousePremiumLoadRowMapper;
        this.chargeCapacityRowMapper = chargeCapacityRowMapper;
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<WarehouseLoadDto> findWarehouseLoadByAccountId(Long accountId) {
        try {
            return jdbcTemplate.query(Queries.SQL_FIND_WAREHOUSE_LOAD_BY_ACCOUNT_ID, warehouseLoadRowMapper, accountId);
        }catch (DataAccessException e) {
            throw crudException(e.toString(),"findWarehouseLoad", "accountId = " + accountId);
        }
    }

    @Override
    public List<PopularItemsDto> findPopularItems(PopularItemsRequestDto popularItems){
        try {
            switch (popularItems.getDateType()){
                case YEAR:
                    return jdbcTemplate.query(Queries.SQL_FIND_POPULAR_ITEMS + Queries.SQL_POP_YEAR +
                            (popularItems.getPopType()==TOP ? Queries.SQL_ATR_POP : Queries.SQL_ATR_UNPOP),
                        popularItemsRowMapper,
                        popularItems.getDate(), popularItems.getQuantity());
                case MONTH:
                    return jdbcTemplate.query(Queries.SQL_FIND_POPULAR_ITEMS + Queries.SQL_POP_MONTH +
                            (popularItems.getPopType()==TOP ? Queries.SQL_ATR_POP : Queries.SQL_ATR_UNPOP),
                        popularItemsRowMapper,
                        popularItems.getDate(), popularItems.getDate(), popularItems.getQuantity());
                default:
                    return jdbcTemplate.query(Queries.SQL_FIND_POPULAR_ITEMS +
                            (popularItems.getPopType()==TOP ? Queries.SQL_ATR_POP : Queries.SQL_ATR_UNPOP),
                        popularItemsRowMapper,
                        popularItems.getQuantity());
            }

        } catch (DataAccessException e) {
            throw crudException(e.toString(),"findPopularItems", "*");
        }
    }

    @Override
    public List<EndingItemsDto> findEndedItemsByAccountId(int minQuantity, Long accountId){
        try {
            return jdbcTemplate.query(Queries.SQL_FIND_ENDED_ITEMS_BY_ACCOUNT_ID, endingItemsRowMapper,
                                      minQuantity, accountId, accountId);
        } catch (DataAccessException e) {
            throw crudException(e.toString(),"findEndedItem", "accountId = " + accountId);
        }
    }

    @Override
    public WarehousePremiumStructDto getPreLoadByAccounId(Long id, Long accountId) {
        try {
            WarehousePremiumStructDto wpld = jdbcTemplate.queryForObject(Queries.SQL_WAREHOUSE_STRUCTURE_PRIMARY,
                warehousePremiumStructRowMapper,id, accountId);
            wpld.setLevel(0);
            ChargeCapacity cc = jdbcTemplate.queryForObject(Queries.SQL_FIND_SUBS_LOAD,
                chargeCapacityRowMapper,
                wpld.getId(), accountId, accountId, accountId, accountId);
            wpld.setCapacity(cc.getCapacity());
            wpld.setCharge(cc.getCharge());
            wpld.setChilds(findSub(id,wpld.getLevel(), accountId));
            return wpld;
        } catch (DataAccessException e) {
            throw crudException(e.toString(),"getPreLoad","accountId=" + accountId);
        }
    }

    private List<WarehousePremiumStructDto> findSub(Long id, int level, Long accountId){
        try{
            List<WarehousePremiumStructDto> wpld = jdbcTemplate.query(Queries.SQL_WAREHOUSE_STRUCTURE_SUB,
                warehousePremiumStructRowMapper, id, accountId);
                for (int i = 0; i < wpld.size(); i++) {
                    wpld.get(i).setLevel(level + 1);

                    ChargeCapacity cc1 = jdbcTemplate.queryForObject(Queries.SQL_FIND_SUBS_LOAD,
                        chargeCapacityRowMapper,
                        wpld.get(i).getId(), accountId, accountId, accountId, accountId);

                    wpld.get(i).setCharge(cc1.getCharge());
                    wpld.get(i).setCapacity(cc1.getCapacity());

                    wpld.get(i).setChilds(findSub(wpld.get(i).getId(), wpld.get(i).getLevel(), accountId));
                    if (wpld.get(i).getChilds().size() == 0){
                        ChargeCapacity cc2 = jdbcTemplate.queryForObject(Queries.SQL_FIND_BOT_LOAD,
                            chargeCapacityRowMapper, wpld.get(i).getId(), accountId);

                        wpld.get(i).setCharge(cc2.getCharge());
                        wpld.get(i).setCapacity(cc2.getCapacity());
                    }
                }
                return wpld;
        } catch (DataAccessException e) {
            throw crudException(e.toString(),"findSub","*");
        }
    }

    private CRUDException crudException(String message, String operation, String attribute) {
        CRUDException exception = new CRUDException(message);
        LOGGER.error("CRUDException exception. Operation:({}) Dashboard ({}) exception. Message: {}", operation, attribute, message);
        return exception;
    }


    class Queries {
        static final String SQL_FIND_POPULAR_ITEMS =
            "SELECT it.name_item AS name, sum(ts.quantity) AS quantity " +
                "FROM transactions ts " +
                "JOIN items it " +
                "ON ts.item_id = it.id " +
                "WHERE type = 'OUT' ";

        static final String SQL_POP_YEAR =
            "AND year(?)=year(ts.timestamp)";
        static final String SQL_POP_MONTH =
            "AND year(?)=year(ts.timestamp) " +
                "AND month(?)=month(ts.timestamp) ";


        static final String SQL_ATR_POP =
            "GROUP BY ts.item_id " +
                "ORDER BY sum(ts.quantity) DESC " +
                "LIMIT ?";
        static final String SQL_ATR_UNPOP =
            "GROUP BY ts.item_id " +
                "ORDER BY sum(ts.quantity) " +
                "LIMIT ? ";

        static final String SQL_FIND_WAREHOUSE_LOAD_BY_ACCOUNT_ID =
            "SELECT wcap.id, ifnull(charge,0) charge, capacity " +
                "FROM " +
                "(SELECT wh.top_warehouse_id id, sum(capacity) capacity, account_id, active " +
                "FROM warehouses wh WHERE account_id=? and active=1 GROUP BY wh.top_warehouse_id) AS wcap " +
                "LEFT OUTER JOIN " +
                "(SELECT top_warehouse_id id, sum(quantity*volume) charge " +
                "FROM saved_items si, items it, warehouses wh " +
                "WHERE si.item_id=it.id and si.warehouse_id=wh.id " +
                "GROUP BY top_warehouse_id) AS wcharge " +
                "ON wcap.id=wcharge.id ";

        static final String SQL_FIND_ENDED_ITEMS_BY_ACCOUNT_ID =
            "SELECT wh.id, wh.name, it.name_item, si.quantity " +
                "FROM saved_items si " +
                "JOIN warehouses wh " +
                "ON si.warehouse_id = wh.id " +
                "JOIN items it " +
                "ON si.item_id = it.id " +
                "WHERE si.quantity <= ? and wh.account_id = ?";

        static final String SQL_WAREHOUSE_STRUCTURE_PRIMARY =
            "SELECT id, name " +
                "FROM warehouses " +
                "WHERE id=? and account_id=?";

        static final String SQL_WAREHOUSE_STRUCTURE_SUB =
            "SELECT id, name " +
                "FROM warehouses " +
                "WHERE parent_id=? and account_id=?";

        static final String SQL_FIND_SUBS_LOAD =
            "SELECT ifnull(sum(capacity), 0) capacity,  ifnull(sum(charge),0) charge FROM  " +
                "(WITH RECURSIVE childs (id, name, parent_id) AS ( " +
                "  SELECT     id, name, parent_id  FROM   warehouses " +
                "  WHERE      parent_id = ? and account_id=? " +
                "  UNION ALL " +
                "  SELECT     wh.id, wh.name, wh.parent_id  FROM warehouses wh " +
                "  INNER JOIN childs  ON wh.parent_id = childs.id " +
                "  WHERE account_id = ?) " +
                "SELECT  a.id, a.name, capacity FROM childs a, warehouses w WHERE a.id=w.id AND is_bottom = 1 AND account_id=?) a " +
                "LEFT OUTER JOIN " +
                "(SELECT warehouse_id id, sum(quantity*volume) charge  " +
                "FROM saved_items si, items it " +
                "WHERE si.id=it.id AND it.account_id=? " +
                "GROUP BY warehouse_id) b " +
                "ON a.id=b.id";

        static final String SQL_FIND_BOT_LOAD=
            "SELECT ifnull(sum(quantity*volume),0) charge, ifnull(capacity, 0) capacity " +
                "FROM saved_items si " +
                "RIGHT JOIN warehouses wh " +
                "ON si.warehouse_id=wh.id " +
                "LEFT JOIN items it " +
                "ON si.item_id=it.id " +
                "WHERE wh.id=? and it.account_id=?";

    }
}
