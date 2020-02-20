package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.dao.impl.DashboardDaoImpl;
import com.ita.if103java.ims.dto.PopularItemsRequestDto;
import com.ita.if103java.ims.dto.WarehousePremiumStructDto;
import com.ita.if103java.ims.entity.DateType;
import com.ita.if103java.ims.entity.PopType;
import com.ita.if103java.ims.exception.dao.DashboardDataNotFoundException;
import com.ita.if103java.ims.mapper.jdbc.EndingItemsRowMapper;
import com.ita.if103java.ims.mapper.jdbc.PopularItemsRowMapper;
import com.ita.if103java.ims.mapper.jdbc.WarehouseLoadRowMapper;
import com.ita.if103java.ims.mapper.jdbc.WarehousePremiumStructRowMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.ArrayList;

import static com.ita.if103java.ims.util.JDBCUtils.getOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DashboardDaoImplTest {

    @Mock
    private WarehouseLoadRowMapper warehouseLoadRowMapper;
    @Mock
    private PopularItemsRowMapper popularItemsRowMapper;
    @Mock
    private EndingItemsRowMapper endingItemsRowMapper;
    @Mock
    private WarehousePremiumStructRowMapper warehousePremiumStructRowMapper;
    @Mock
    JdbcTemplate jdbcTemplate;

    @InjectMocks
    DashboardDaoImpl dashboardDaoImpl;

    private Long fakeAccountId = 1L;
    private Long fakeWarehouseId = 4L;
    private int fakeMinQuantity = 5;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testWarehouseLoad(){
        dashboardDaoImpl.findWarehouseLoadByAccountId(fakeAccountId);
        verify(jdbcTemplate, times(1))
            .query(
                DashboardDaoImpl.Queries.SQL_FIND_WAREHOUSE_LOAD_BY_ACCOUNT_ID, warehouseLoadRowMapper, fakeAccountId
            );
    }

    @Test
    public void testWarehouseLoad_DBNotResponding(){
        when(jdbcTemplate.query(
            DashboardDaoImpl.Queries.SQL_FIND_WAREHOUSE_LOAD_BY_ACCOUNT_ID, warehouseLoadRowMapper, -fakeAccountId))
            .thenThrow(EmptyResultDataAccessException.class);
        assertThrows(DashboardDataNotFoundException.class, () -> dashboardDaoImpl.findWarehouseLoadByAccountId(-fakeAccountId));

    }

    @Test
    public void testPopularItems_popularDuringYear() {
        PopularItemsRequestDto popularItems =
            new PopularItemsRequestDto(3, DateType.YEAR, PopType.TOP, "17-02-2020");

        dashboardDaoImpl.findPopularItems(popularItems, fakeAccountId);

        verify(jdbcTemplate, times(1))
            .query(
                DashboardDaoImpl.Queries.SQL_FIND_POPULAR_ITEMS + DashboardDaoImpl.Queries.SQL_POP_YEAR +
                    DashboardDaoImpl.Queries.SQL_ATR_POP,
                popularItemsRowMapper,
                fakeAccountId, popularItems.getDate(), popularItems.getQuantity()
            );
    }

    @Test
    public void testPopularItems_unpopularDuringMonth() {
        PopularItemsRequestDto popularItems =
            new PopularItemsRequestDto(9, DateType.MONTH, PopType.BOT, "17-02-2019");

        dashboardDaoImpl.findPopularItems(popularItems, fakeAccountId);

        verify(jdbcTemplate, times(1))
            .query(
                DashboardDaoImpl.Queries.SQL_FIND_POPULAR_ITEMS +
                    DashboardDaoImpl.Queries.SQL_POP_MONTH + DashboardDaoImpl.Queries.SQL_ATR_UNPOP,
                popularItemsRowMapper,
                fakeAccountId, popularItems.getDate(), popularItems.getDate(), popularItems.getQuantity()
            );
    }


    @Test
    public void testPopularItems_unpopularDuringWholeTime() {
        PopularItemsRequestDto popularItems =
            new PopularItemsRequestDto(15, DateType.ALL, PopType.BOT, "13-06-2019");

        dashboardDaoImpl.findPopularItems(popularItems, fakeAccountId);

        verify(jdbcTemplate, times(1))
            .query(
                DashboardDaoImpl.Queries.SQL_FIND_POPULAR_ITEMS + DashboardDaoImpl.Queries.SQL_ATR_UNPOP,
                popularItemsRowMapper,
                fakeAccountId, popularItems.getQuantity()
            );
    }

    @Test
    public void testPopularItemsException() {
        PopularItemsRequestDto popularItems =
            new PopularItemsRequestDto(15, DateType.ALL, PopType.BOT, "13-06-2019");
        when(jdbcTemplate
            .query(
                DashboardDaoImpl.Queries.SQL_FIND_POPULAR_ITEMS + DashboardDaoImpl.Queries.SQL_ATR_UNPOP,
                popularItemsRowMapper,
                fakeAccountId, popularItems.getQuantity()
            ))
            .thenThrow(EmptyResultDataAccessException.class);

        assertThrows(DashboardDataNotFoundException.class, () ->
            dashboardDaoImpl.findPopularItems(popularItems, fakeAccountId));
    }

    @Test
    public void testEndedItems(){
        PageRequest pageable = PageRequest.of(1, 4, Sort.by("quantity,DESC"));

        when(jdbcTemplate.queryForObject(DashboardDaoImpl.Queries.SQL_ROW_COUNT,
            new Object[]{fakeMinQuantity, fakeAccountId}, Integer.class)).thenReturn(1);

        dashboardDaoImpl.findEndedItemsByAccountId(pageable, fakeMinQuantity, fakeAccountId);

        verify(jdbcTemplate, times(1))
            .query(
                String.format(DashboardDaoImpl.Queries.SQL_FIND_ENDED_ITEMS_BY_ACCOUNT_ID, getOrder(pageable.getSort())),
                endingItemsRowMapper, fakeMinQuantity, fakeAccountId, pageable.getPageSize(), pageable.getOffset()
            );
        verify(jdbcTemplate, times(1))
            .queryForObject(
                DashboardDaoImpl.Queries.SQL_ROW_COUNT,
                new Object[]{fakeMinQuantity, fakeAccountId}, Integer.class
            );
    }
    @Test
    public void testEndedItems_DBNotRespondToFindEndedItems() {
        PageRequest pageable = PageRequest.of(1, 4, Sort.by("quantity,DESC"));

        when(jdbcTemplate
            .query(
                String.format(DashboardDaoImpl.Queries.SQL_FIND_ENDED_ITEMS_BY_ACCOUNT_ID, getOrder(pageable.getSort())),
                endingItemsRowMapper, fakeMinQuantity, fakeAccountId, pageable.getPageSize(), pageable.getOffset()
            )
        ).thenThrow(DashboardDataNotFoundException.class);

        assertThrows(DashboardDataNotFoundException.class, () ->
            dashboardDaoImpl.findEndedItemsByAccountId(pageable, fakeMinQuantity, fakeAccountId));
    }
    @Test
    public void testEndedItems_DBNotRespondToFindRowCount() {
        PageRequest pageable = PageRequest.of(1, 4, Sort.by("quantity,DESC"));

        when(jdbcTemplate
            .queryForObject(
                DashboardDaoImpl.Queries.SQL_ROW_COUNT, new Object[]{fakeMinQuantity, fakeAccountId}, Integer.class
            )
        ).thenThrow(DashboardDataNotFoundException.class);

        assertThrows(DashboardDataNotFoundException.class, () ->
            dashboardDaoImpl.findEndedItemsByAccountId(pageable, fakeMinQuantity, fakeAccountId));
    }
    @Test
    public void testPremiumLoad(){
        when(jdbcTemplate.queryForObject(
            DashboardDaoImpl.Queries.SQL_WAREHOUSE_STRUCTURE_PRIMARY,
            warehousePremiumStructRowMapper, fakeWarehouseId, fakeAccountId
        )).thenReturn(new WarehousePremiumStructDto());

        when(jdbcTemplate.query(DashboardDaoImpl.Queries.SQL_WAREHOUSE_STRUCTURE_SUB,
            warehousePremiumStructRowMapper, fakeWarehouseId, fakeAccountId)).thenReturn(new ArrayList<>());

        dashboardDaoImpl.getPreLoadByAccounId(fakeWarehouseId, fakeAccountId);

        verify(jdbcTemplate, times(1))
            .queryForObject(
                DashboardDaoImpl.Queries.SQL_WAREHOUSE_STRUCTURE_PRIMARY,
                warehousePremiumStructRowMapper, fakeWarehouseId, fakeAccountId
            );
    }

    @Test
    public void testPremiumLoad_DBNotRespondToFindPrimaryWarehouse(){
        when(jdbcTemplate.queryForObject(
            DashboardDaoImpl.Queries.SQL_WAREHOUSE_STRUCTURE_PRIMARY,
            warehousePremiumStructRowMapper, fakeWarehouseId, fakeAccountId
        )).thenThrow(DashboardDataNotFoundException.class);

        assertThrows(DashboardDataNotFoundException.class, () ->
            dashboardDaoImpl.getPreLoadByAccounId(fakeWarehouseId, fakeAccountId));
    }

    @Test
    public void testPremiumLoad_DBNotRespondToFindWarehouseStructure(){
        when(jdbcTemplate.queryForObject(
            DashboardDaoImpl.Queries.SQL_WAREHOUSE_STRUCTURE_PRIMARY,
            warehousePremiumStructRowMapper, fakeWarehouseId, fakeAccountId
        )).thenReturn(new WarehousePremiumStructDto());

        when(jdbcTemplate.query(DashboardDaoImpl.Queries.SQL_WAREHOUSE_STRUCTURE_SUB,
            warehousePremiumStructRowMapper, fakeWarehouseId, fakeAccountId)).thenThrow(DashboardDataNotFoundException.class);

        assertThrows(DashboardDataNotFoundException.class, () ->
            dashboardDaoImpl.getPreLoadByAccounId(fakeWarehouseId, fakeAccountId));
    }
}
