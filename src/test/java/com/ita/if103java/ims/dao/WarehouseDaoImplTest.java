package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.config.GeneratedKeyHolderFactory;
import com.ita.if103java.ims.dao.impl.WarehouseDaoImpl;
import com.ita.if103java.ims.entity.Warehouse;
import com.ita.if103java.ims.exception.dao.CRUDException;
import com.ita.if103java.ims.mapper.jdbc.WarehouseRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class WarehouseDaoImplTest {
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private KeyHolder keyHolder;
    @Mock
    private Connection connection;
    @Mock
    private DataSource dataSource;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private GeneratedKeyHolderFactory generatedKeyHolderFactory;
    @Mock
    WarehouseRowMapper warehouseRowMapper;

    @InjectMocks
    private WarehouseDaoImpl warehouseDao;

    private Warehouse warehouse;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.initMocks(this);
        when(this.jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(this.dataSource.getConnection()).thenReturn(connection);
        when(this.connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
        when(this.generatedKeyHolderFactory.newKeyHolder()).thenReturn(keyHolder);
        when(this.keyHolder.getKey()).thenReturn(1L);
        when(this.jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenReturn(1);

        warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Stock");
        warehouse.setInfo("universal");
        warehouse.setCapacity(30);
        warehouse.setBottom(true);
        warehouse.setParentID(5L);
        warehouse.setAccountID(2L);
        warehouse.setTopWarehouseID(4L);
        warehouse.setActive(true);

        warehouseDao.create(warehouse);


//        Warehouse warehouseTop = new Warehouse();
//        warehouseTop.setName("TopStock");
//        warehouseTop.setInfo("universal");
//        warehouseTop.setCapacity(0);
//        warehouseTop.setBottom(false);
//        warehouseTop.setParentID(null);
//        warehouseTop.setAccountID(2L);
//        warehouseTop.setTopWarehouseID(null);
//        warehouseTop.setActive(true);
//
//        warehouseDao.create(warehouseTop);
    }

    @Test
    void testCreate_successFlow() {
        assertEquals(warehouse, warehouseDao.create(warehouse));
        assertNotNull(warehouse.getId());
        assertTrue(warehouse.isActive());
    }

    @Test
    void testCreate_omittedNotNullFields() {
        when(this.keyHolder.getKey()).thenReturn(null);
        warehouse = new Warehouse();
        assertThrows(CRUDException.class, () -> warehouseDao.create(warehouse));
    }

    @Test
    void testFindById_successFlow() {
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<WarehouseRowMapper>any(), anyLong(), anyLong()))
            .thenReturn(warehouse);

        assertEquals(warehouse, warehouseDao.findById(warehouse.getId(), warehouse.getAccountID()));
    }

    @Test
    void testUpdate_successFlow() {
        Warehouse updatedWarehouse = warehouse;

        when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(1);
        assertEquals(updatedWarehouse, warehouseDao.update(updatedWarehouse));
    }

    @Test
    public void testDeleteByAccountId() {
        warehouseDao.hardDelete(1L);
        verify(jdbcTemplate, times(1)).update(anyString(), eq(1L));
    }
}



