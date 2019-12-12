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
    private ChargeCapacityRowMapper chargeCapacityRowMapper;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DashboardDaoImpl(WarehouseLoadRowMapper warehouseLoadRowMapper,
                            EndingItemsRowMapper endingItemsRowMapper,
                            PopularItemsRowMapper popularItemsRowMapper,
                            WarehousePremiumStructRowMapper warehousePremiumStructRowMapper,
                            ChargeCapacityRowMapper chargeCapacityRowMapper,
                            JdbcTemplate jdbcTemplate) {
        this.warehousePremiumStructRowMapper = warehousePremiumStructRowMapper;
        this.endingItemsRowMapper = endingItemsRowMapper;
        this.popularItemsRowMapper = popularItemsRowMapper;
        this.warehouseLoadRowMapper = warehouseLoadRowMapper;
        this.chargeCapacityRowMapper = chargeCapacityRowMapper;
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<WarehouseLoadDto> findWarehouseLoadByAccountId(Long accountId) {
        try {
            return jdbcTemplate.query(Queries.SQL_FIND_WAREHOUSE_LOAD_BY_ACCOUNT_ID, warehouseLoadRowMapper, accountId);
        }catch (DataAccessException e) {
            throw crudException(e,"findWarehouseLoad", "accountId = " + accountId);
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
            throw crudException(e,"findPopularItems", "*");
        }
    }

    @Override
    public List<EndingItemsDto> findEndedItemsByAccountId(int minQuantity, Long accountId){
        try {
            return jdbcTemplate.query(Queries.SQL_FIND_ENDED_ITEMS_BY_ACCOUNT_ID, endingItemsRowMapper,
                minQuantity, accountId);
        } catch (DataAccessException e) {
            throw crudException(e, "findEndedItem", "accountId = " + accountId);
        }
    }

    @Override
    public WarehousePremiumStructDto getPreLoadByAccounId(Long id, Long accountId) {
        try {
            WarehousePremiumStructDto wpld = jdbcTemplate.queryForObject(Queries.SQL_WAREHOUSE_STRUCTURE_PRIMARY,
                warehousePremiumStructRowMapper,id, accountId);
            wpld.setLevel(0);

            wpld.setChilds(findWarehouseStructureAndBottomLoad(id, id, wpld.getLevel(), accountId));

            recursiceWarehouseDataFiller(wpld);

            return wpld;
        } catch (DataAccessException e) {
            throw crudException(e,"getPreLoad","accountId=" + accountId);
        }
    }

    private List<WarehousePremiumStructDto> findWarehouseStructureAndBottomLoad(Long id, Long topWarehouseId,
                                                                                int level, Long accountId){
        try{
            List<WarehousePremiumStructDto> wpld = jdbcTemplate.query(Queries.SQL_WAREHOUSE_STRUCTURE_SUB,
                warehousePremiumStructRowMapper, id, accountId);
            for (WarehousePremiumStructDto warehouseItem : wpld) {
                warehouseItem.setLevel(level + 1);
                warehouseItem.setChilds(findWarehouseStructureAndBottomLoad(warehouseItem.getId(), topWarehouseId,
                    warehouseItem.getLevel(), accountId));
                if (warehouseItem.getChilds().size()==0) {
                    ChargeCapacity chargeCapacity = jdbcTemplate.queryForObject(Queries.SQL_FIND_BOT_CAPACITY_CHARGE,
                        chargeCapacityRowMapper, topWarehouseId, warehouseItem.getId(), accountId);
                    warehouseItem.setCharge(chargeCapacity.getCharge());
                    warehouseItem.setCapacity(chargeCapacity.getCapacity());
                }
            }
            return wpld;
        } catch (DataAccessException e) {
            throw crudException(e,"findWarehouseStructure","*");
        }
    }

    private void recursiceWarehouseDataFiller(WarehousePremiumStructDto wp){
        for(WarehousePremiumStructDto child : wp.getChilds()) {
            recursiceWarehouseDataFiller(child);
        }
        for(WarehousePremiumStructDto child : wp.getChilds()) {
            wp.setCharge(wp.getCharge() + child.getCharge());
            wp.setCapacity(wp.getCapacity() + child.getCapacity());
        }
    }

    private CRUDException crudException(Exception e, String operation, String attribute) {
        CRUDException exception = new CRUDException(
            "CRUDException exception. Operation:(" + operation + ") Attribute (" + attribute + ").", e);
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
            "SELECT top_warehouse_id id,  ifnull(sum(charge),0) charge, ifnull(sum(capacity),0) capacity FROM " +
                "(SELECT warehouse_id, it.account_id, sum(quantity*volume) charge " +
                "FROM saved_items si " +
                "JOIN items it " +
                "ON si.item_id = it.id " +
                "JOIN warehouses wh " +
                "ON si.warehouse_id = wh.id " +
                "GROUP BY warehouse_id) AS  cha " +
                "RIGHT JOIN " +
                "(SELECT id, capacity, account_id, top_warehouse_id " +
                "FROM warehouses " +
                "WHERE is_bottom=1) AS cap " +
                "ON  cha.warehouse_id=cap.id AND  cha.account_id=cap.account_id " +
                "WHERE cap.account_id=? " +
                "GROUP BY top_warehouse_id";

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

        static final String SQL_FIND_BOT_CAPACITY_CHARGE=
            "SELECT id, ifnull(sum(charge),0) charge, ifnull(sum(capacity),0) capacity FROM " +
                "(SELECT warehouse_id, it.account_id, sum(quantity*volume) charge " +
                "FROM saved_items si " +
                "JOIN items it  " +
                "ON si.item_id = it.id " +
                "JOIN warehouses wh " +
                "ON si.warehouse_id = wh.id " +
                "GROUP BY warehouse_id) AS cha " +
                "RIGHT JOIN " +
                "(SELECT id, capacity, account_id " +
                "FROM warehouses " +
                "WHERE is_bottom=1 and top_warehouse_id = ?) AS cap " +
                "ON cha.warehouse_id=cap.id AND cha.account_id=cap.account_id " +
                "WHERE id = ? AND cap.account_id = ?";
    }
}
