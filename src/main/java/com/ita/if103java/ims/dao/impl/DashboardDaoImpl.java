package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.DashboardDao;
import com.ita.if103java.ims.dto.PopularItemsRequestDto;
import com.ita.if103java.ims.dto.PopularItemsDto;
import com.ita.if103java.ims.dto.EndingItemsDto;
import com.ita.if103java.ims.dto.WarehouseLoadDto;
import com.ita.if103java.ims.dto.WarehousePremiumStructDto;
import com.ita.if103java.ims.exception.CRUDException;
import com.ita.if103java.ims.mapper.jdbc.PopularityListRowMapper;
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
import java.util.ArrayList;
import java.util.List;

import static com.ita.if103java.ims.entity.PopType.TOP;

@Repository
public class DashboardDaoImpl implements DashboardDao {
    private final static Logger LOGGER = LoggerFactory.getLogger(DashboardDaoImpl.class);
    private WarehouseLoadRowMapper warehouseLoadRowMapper;
    private PopularityListRowMapper popularityListRowMapper;
    private EndingItemsRowMapper endingItemsRowMapper;
    private WarehousePremiumStructRowMapper warehousePremiumLoadMapper;
    private WarehousePremiumLoadRowMapper warehousePremiumLoadRowMapper;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DashboardDaoImpl(WarehouseLoadRowMapper warehouseLoadRowMapper,
                            EndingItemsRowMapper endingItemsRowMapper,
                            PopularityListRowMapper popularityListRowMapper,
                            WarehousePremiumStructRowMapper warehousePremiumLoadMapper,
                            WarehousePremiumLoadRowMapper warehousePremiumLoadRowMapper,
                            JdbcTemplate jdbcTemplate) {
        this.warehousePremiumLoadMapper = warehousePremiumLoadMapper;
        this.endingItemsRowMapper = endingItemsRowMapper;
        this.popularityListRowMapper = popularityListRowMapper;
        this.warehouseLoadRowMapper = warehouseLoadRowMapper;
        this.warehousePremiumLoadRowMapper = warehousePremiumLoadRowMapper;
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<WarehouseLoadDto> findWarehouseLoad() {
        try {
            return jdbcTemplate.query(Queries.SQL_FIND_WAREHOUSE_LOAD, warehouseLoadRowMapper);
        }catch (DataAccessException e) {
            throw new CRUDException("DashboardDaoImpl failed to find WarehouseLoad", e);
        }
    }

    @Override
    public List<PopularItemsDto> findPopularItems(PopularItemsRequestDto popularItems){
        try {
            switch (popularItems.getDateType()){
                case YEAR:
                    return jdbcTemplate.query(Queries.SQL_FIND_POPULAR_ITEMS + Queries.SQL_POP_YEAR +
                            (popularItems.getPopType()==TOP ? Queries.SQL_ATR_POP : Queries.SQL_ATR_UNPOP),
                            popularityListRowMapper,
                            popularItems.getDate(), popularItems.getQuantity());
                case MONTH:
                    return jdbcTemplate.query(Queries.SQL_FIND_POPULAR_ITEMS + Queries.SQL_POP_MONTH +
                            (popularItems.getPopType()==TOP ? Queries.SQL_ATR_POP : Queries.SQL_ATR_UNPOP),
                            popularityListRowMapper,
                            popularItems.getDate(), popularItems.getDate(), popularItems.getQuantity());
                default:
                    return jdbcTemplate.query(Queries.SQL_FIND_POPULAR_ITEMS +
                            (popularItems.getPopType()==TOP ? Queries.SQL_ATR_POP : Queries.SQL_ATR_UNPOP),
                            popularityListRowMapper, popularItems.getQuantity());
            }

        } catch (DataAccessException e) {
            throw new CRUDException("DashboardDaoImpl failed to find PopularItems", e);
        }
    }

    @Override
    public List<EndingItemsDto> findEndedItems(int minQuantity){
        try {
            return jdbcTemplate.query(Queries.SQL_FIND_ENDED_ITEMS, endingItemsRowMapper, minQuantity);
        } catch (DataAccessException e) {
            throw new CRUDException("DashboardDaoImpl failed to find EndedItems", e);
        }
    }

    @Override
    public List<WarehousePremiumStructDto> getPreStructure(Long id) {
        try {
            WarehousePremiumStructDto wpld = jdbcTemplate.queryForObject(Queries.SQL_WAREHOUSE_STRUCTURE_PRIMARY,
                warehousePremiumLoadMapper,id);
            wpld.setLevel(0);
            SupFunc supFunc = new SupFunc();
            wpld.setChilds(supFunc.findSub(id,wpld.getLevel()));
            List<WarehousePremiumStructDto> list = new ArrayList<>();
            supFunc.fillList(wpld, list);
            return list;
        } catch (DataAccessException e) {
            throw new CRUDException("DashboardDaoImpl failed to find PreWarehouseTop",e);
        }
    }

    @Override
    public List<WarehouseLoadDto> getPreLoad(Long id){
        try {
            return jdbcTemplate.query(Queries.SQL_FIND_SUBS_LOAD,
                warehousePremiumLoadRowMapper, id);
        }catch (DataAccessException e) {
            throw new CRUDException("DashboardDaoImpl failed to find PreWarehouseLoad",e);
        }
    }
    class SupFunc {
        public List<WarehousePremiumStructDto> findSub(Long id, int level){
            try{
                List<WarehousePremiumStructDto> wpld = jdbcTemplate.query(Queries.SQL_WAREHOUSE_STRUCTURE_SUB,
                    warehousePremiumLoadMapper,id);
                for(int i=0;i<wpld.size();i++){
                    wpld.get(i).setLevel(level+1);
                    wpld.get(i).setChilds(findSub(wpld.get(i).getId(), wpld.get(i).getLevel() ));
                }
                return wpld;
            } catch (DataAccessException e) {
                throw new CRUDException("DashboardDaoImpl failed to find PreWarehouseChilds",e);
            }
        }

        public void fillList(WarehousePremiumStructDto wpld, List<WarehousePremiumStructDto> list){
            list.add(wpld);
            for(int i = 0; i < wpld.getChilds().size(); i++) {
                fillList(wpld.getChilds().get(i),list);
            }
        }
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

        static final String SQL_FIND_WAREHOUSE_LOAD =
            "SELECT wh.top_warehouse_id, sum(quantity*volume) AS charge, sum(wh.capacity) AS capacity " +
                "FROM saved_items si " +
                "JOIN items it " +
                "ON si.item_id=it.id " +
                "JOIN warehouses wh " +
                "ON si.warehouse_id=wh.id " +
                "WHERE wh.is_bottom=1 " +
                "GROUP BY wh.top_warehouse_id";

        static final String SQL_FIND_ENDED_ITEMS =
            "SELECT wh.id, wh.name, it.name_item, si.quantity " +
                "FROM saved_items si " +
                "JOIN warehouses wh " +
                "ON si.warehouse_id = wh.id " +
                "JOIN items it " +
                "ON si.item_id = it.id " +
                "WHERE si.quantity <= ?";

        static final String SQL_WAREHOUSE_STRUCTURE_PRIMARY =
            "SELECT id, name " +
                "FROM warehouses " +
                "WHERE id=?";

        static final String SQL_WAREHOUSE_STRUCTURE_SUB =
            "SELECT id, name " +
                "FROM warehouses " +
                "WHERE parent_id=?";

        static final String SQL_FIND_SUBS_LOAD =
            "SELECT wh.id, sum(si.quantity * it.volume) AS charge, wh.capacity " +
                "FROM warehouses wh " +
                "JOIN saved_items si " +
                "ON wh.id = si.warehouse_id " +
                "JOIN items it " +
                "ON it.id = si.item_id " +
                "WHERE wh.top_warehouse_id=? " +
                "GROUP BY wh.id";
    }
}
