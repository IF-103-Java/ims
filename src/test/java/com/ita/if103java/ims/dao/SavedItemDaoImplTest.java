package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.config.GeneratedKeyHolderFactory;
import com.ita.if103java.ims.dao.impl.SavedItemDaoImpl;
import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.exception.dao.CRUDException;
import com.ita.if103java.ims.exception.dao.SavedItemNotFoundException;
import com.ita.if103java.ims.mapper.jdbc.SavedItemRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@SpringBootTest
public class SavedItemDaoImplTest {
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

    @InjectMocks
    SavedItemDaoImpl savedItemDao;

    SavedItem trout;
    List<SavedItem> savedItems;
    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.initMocks(this);
        when(this.jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(this.dataSource.getConnection()).thenReturn(this.connection);
        when(this.connection.prepareStatement(anyString(), anyInt())).thenReturn(this.preparedStatement);
        when(this.generatedKeyHolderFactory.newKeyHolder()).thenReturn(keyHolder);
        when(this.keyHolder.getKey()).thenReturn(1L);
        when(this.jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenReturn(1);
        trout = new SavedItem();
        trout.setId(70L);
        trout.setQuantity(5);
        trout.setItemId(109L);
        trout.setWarehouseId(37L);

        savedItems = new ArrayList<>();
        SavedItem trout = new SavedItem();
        trout.setId(70L);
        trout.setQuantity(5);
        trout.setItemId(109L);
        trout.setWarehouseId(37L);

        savedItems.add(trout);

        SavedItem salmon = new SavedItem();
        salmon.setId(71L);
        salmon.setQuantity(5);
        salmon.setItemId(110L);
        salmon.setWarehouseId(38L);

        savedItems.add(salmon);

        SavedItem catfish = new SavedItem();
        catfish.setId(72L);
        catfish.setQuantity(5);
        catfish.setItemId(111L);
        catfish.setWarehouseId(39L);

        savedItems.add(catfish);
    }

    @Test
    void getSavedItems_successFlow(){
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<SavedItemRowMapper>any())).thenReturn(savedItems);
        assertEquals(savedItems, savedItemDao.getSavedItems());
    }

    @Test
    void getSavedItems_omittedFlowCRUDException(){
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<SavedItemRowMapper>any())).thenThrow(new DataAccessException(""){});
        assertThrows(CRUDException.class,()-> savedItemDao.getSavedItems());
    }

    @Test
    void findSavedItemById_successFlow(){
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<SavedItemRowMapper>any(), anyLong())).thenReturn(trout);
        assertEquals(trout, savedItemDao.findSavedItemById(trout.getId()));
    }

    @Test
    void findSavedItemById_omittedFlow(){
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<SavedItemRowMapper>any(), anyLong()))
            .thenThrow(new EmptyResultDataAccessException(1){});
        SavedItemNotFoundException exception = assertThrows(SavedItemNotFoundException.class,
            ()-> savedItemDao.findSavedItemById(trout.getId()));
        assertEquals("Failed to get savedItem during `select` {id = " + trout.getId() + "}", exception.getMessage());
    }

    @Test
    void findSavedItemById_omittedFlowCRUDException(){
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<SavedItemRowMapper>any(), anyLong()))
            .thenThrow(new DataAccessException(""){});
        CRUDException exception = assertThrows(CRUDException.class,
            ()-> savedItemDao.findSavedItemById(trout.getId()));
        assertEquals("Failed during `select` {id = " + trout.getId() + "}", exception.getMessage());
    }

    @Test
    void findSavedItemByItemId_successFlow(){
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<SavedItemRowMapper>any(), anyLong())).thenReturn(savedItems);
        assertEquals(savedItems, savedItemDao.findSavedItemByItemId(trout.getItemId()));
    }

    @Test
    void findSavedItemByItemId_omittedFlow(){
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<SavedItemRowMapper>any(), anyLong())).thenThrow(new EmptyResultDataAccessException(1){});
        SavedItemNotFoundException exception = assertThrows(SavedItemNotFoundException.class,
            ()-> savedItemDao.findSavedItemByItemId(trout.getItemId()));
        assertEquals("Failed to get savedItem during `select` {item_id = " + trout.getItemId() +
            "}", exception.getMessage());
    }

    @Test
    void findSavedItemByItemId_omittedFlowCRUDException(){
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<SavedItemRowMapper>any(), anyLong())).thenThrow(new DataAccessException(""){});
        CRUDException exception = assertThrows(CRUDException.class,
            ()-> savedItemDao.findSavedItemByItemId(trout.getItemId()));
        assertEquals("Failed during `select` {item_id = " + trout.getItemId() + "}", exception.getMessage());
    }


    @Test
    void findSavedItemByWarehouseId_successFlow(){
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<SavedItemRowMapper>any(), anyLong())).thenReturn(savedItems);
        assertEquals(savedItems, savedItemDao.findSavedItemByWarehouseId(trout.getWarehouseId()));
    }

    @Test
    void findSavedItemByWarehouseId_omittedFlow(){
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<SavedItemRowMapper>any(), anyLong())).thenThrow(new EmptyResultDataAccessException(1){});
        SavedItemNotFoundException exception = assertThrows(SavedItemNotFoundException.class,
            ()->savedItemDao.findSavedItemByWarehouseId(trout.getWarehouseId()));
        assertEquals("Failed to get savedItem during `select` {warehouse_id = " + trout.getWarehouseId() +
            "}", exception.getMessage());
    }

    @Test
    void findSavedItemByWarehouseId_omittedFlowCRUDException(){
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<SavedItemRowMapper>any(), anyLong())).thenThrow(new DataAccessException(""){});
        CRUDException exception = assertThrows(CRUDException.class,
            ()->savedItemDao.findSavedItemByWarehouseId(trout.getWarehouseId()));
        assertEquals("Failed during `select` {warehouse_id = " + trout.getWarehouseId() + "}", exception.getMessage());
    }

    @Test
    void existSavedItemByWarehouseId_successFlow(){
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<SavedItemRowMapper>any(), anyLong())).thenReturn(savedItems);
        assertEquals(true, savedItemDao.existSavedItemByWarehouseId(trout.getWarehouseId()));
        verify(jdbcTemplate, times(1)).query(anyString(), ArgumentMatchers.<SavedItemRowMapper>any(), anyLong());
    }

    @Test
    void existSavedItemByWarehouseId_omittedFlowEmptyResult(){
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<SavedItemRowMapper>any(), anyLong())).thenThrow(
            EmptyResultDataAccessException.class);
        assertEquals(false, savedItemDao.existSavedItemByWarehouseId(trout.getWarehouseId()));
        verify(jdbcTemplate, times(1)).query(anyString(), ArgumentMatchers.<SavedItemRowMapper>any(), anyLong());
    }

    @Test
    void existSavedItemByWarehouseId_omittedFlow(){
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<SavedItemRowMapper>any(), anyLong())).thenThrow(
            EmptyResultDataAccessException.class);
        assertEquals(false, savedItemDao.existSavedItemByWarehouseId(trout.getWarehouseId()));
        verify(jdbcTemplate, times(1)).query(anyString(), ArgumentMatchers.<SavedItemRowMapper>any(), anyLong());
    }

    @Test
    void existSavedItemByWarehouseId_omittedFlowCRUDException(){
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<SavedItemRowMapper>any(), anyLong())).thenThrow(new DataAccessException(""){});
        CRUDException exception = assertThrows(CRUDException.class,
            ()->savedItemDao.existSavedItemByWarehouseId(trout.getWarehouseId()));
        assertEquals("Failed during `select` {warehouse_id = " + trout.getWarehouseId() + "}", exception.getMessage());
    }
    @Test
    void addSavedItem_successFlow(){
        SavedItem savedItem = new SavedItem();
        assertEquals(savedItem, savedItemDao.addSavedItem(savedItem));
        assertNotNull(savedItem.getId());
    }

    @Test
    void addSavedItem_omittedFlowNotNullFields(){
        when(this.keyHolder.getKey()).thenReturn(null);
        SavedItem savedItem = new SavedItem();
        savedItem.setWarehouseId(37L);
       assertThrows(CRUDException.class, ()->savedItemDao.addSavedItem(savedItem));
    }

    @Test
    void outComeSavedItem_successFlow(){
        SavedItem savedItem = new SavedItem();
        savedItem.setId(2L);
        savedItem.setQuantity(5);
        int quantity = 5;
        when(jdbcTemplate.update(anyString(), anyInt(), anyLong())).thenReturn(1);
        assertEquals(true, savedItemDao.outComeSavedItem(savedItem, quantity));
    }

    @Test
    void outComeSavedItem_omittedFlowNotFound(){
        SavedItem savedItem = new SavedItem();
        savedItem.setId(2L);
        savedItem.setQuantity(5);
        int quantity = 5;
        when(jdbcTemplate.update(anyString(), anyInt(), anyLong())).thenReturn(0);
        SavedItemNotFoundException exception = assertThrows(SavedItemNotFoundException.class,
            ()->savedItemDao.outComeSavedItem(savedItem, quantity));
        assertEquals("Failed to get savedItem during `update` {quantity = " + savedItem.getQuantity() + "warehouse_id" +
            "}", exception.getMessage());
    }

    @Test
    void outComeSavedItem_omittedFlowCRUDException(){
        SavedItem savedItem = new SavedItem();
        savedItem.setId(2L);
        savedItem.setQuantity(5);
        int quantity = 5;
        when(jdbcTemplate.update(anyString(), anyInt(), anyLong())).thenThrow(new DataAccessException(""){});
        CRUDException exception = assertThrows(CRUDException.class,
            ()->savedItemDao.outComeSavedItem(savedItem, quantity));
        assertEquals("Error during `update` {quantity = " + savedItem.getQuantity() + "warehouse_id" + "}", exception.getMessage());
    }

    @Test
    void updateSavedItem_successFlow(){
        Long warehouseId = 40L;
        Long savedItemId = 108L;
        when(jdbcTemplate.update(anyString(), anyLong(), anyLong())).thenReturn(1);
        assertEquals(true, savedItemDao.updateSavedItem(warehouseId, savedItemId));
    }

    @Test
    void updateSavedItem_omittedFlowNotFound(){
        Long warehouseId = 40L;
        Long savedItemId = 108L;
        when(jdbcTemplate.update(anyString(), anyLong(), anyLong())).thenReturn(0);
        SavedItemNotFoundException exception = assertThrows(SavedItemNotFoundException.class,
            ()->savedItemDao.updateSavedItem(warehouseId, savedItemId));
        assertEquals("Failed to get savedItem during `update` {warehouse_id = " + warehouseId + "id" + savedItemId +
            "}", exception.getMessage());
    }

    @Test
    void updateSavedItem_omittedFlowCRUDException(){
        Long warehouseId = 40L;
        Long savedItemId = 108L;
        when(jdbcTemplate.update(anyString(), anyLong(), anyLong())).thenThrow(new DataAccessException(""){});
        CRUDException exception = assertThrows(CRUDException.class,
            ()->savedItemDao.updateSavedItem(warehouseId, savedItemId));
        assertEquals("Error during `update` {warehouse_id = " + warehouseId + " id " + savedItemId + "}", exception.getMessage());
    }

    @Test
    void deleteSavedItem_successFlow(){
        Long savedItemId = 38L;
        when(jdbcTemplate.update(anyString(),  anyLong())).thenReturn(1);
        assertEquals(true, savedItemDao.deleteSavedItem(savedItemId));
    }

    @Test
    void deleteSavedItem_omittedFlowNotFound(){
        Long savedItemId = 38L;
        when(jdbcTemplate.update(anyString(),  anyLong())).thenReturn(0);
        SavedItemNotFoundException exception = assertThrows(SavedItemNotFoundException.class,
            ()->savedItemDao.deleteSavedItem(savedItemId));
        assertEquals("Failed to get soft delete savedItem during `delete` {id = " + savedItemId + "}", exception.getMessage());
    }

    @Test
    void deleteSavedItem_omittedFlowCRUDException(){
        Long savedItemId = 38L;
        when(jdbcTemplate.update(anyString(),  anyLong())).thenThrow(new DataAccessException(""){});
        CRUDException exception = assertThrows(CRUDException.class,
            ()->savedItemDao.deleteSavedItem(savedItemId));
        assertEquals("Error during  `delete` {id = " + savedItemId + "}", exception.getMessage());
    }

    @Test
    void findSavedItemByItemIdAndWarehouseId_successFlow(){
        SavedItem savedItem = new SavedItem();
        Long itemId = 18L;
        Long warehouseId = 40L;
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<SavedItemRowMapper>any(), anyLong(),
            anyLong())).thenReturn(savedItem);
        assertEquals(Optional.of(savedItem), savedItemDao.findSavedItemByItemIdAndWarehouseId(itemId, warehouseId));
    }

    @Test
    void findSavedItemByItemIdAndWarehouseId_omittedFlowNotFound(){
        Long itemId = 18L;
        Long warehouseId = 40L;
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<SavedItemRowMapper>any(), anyLong(),
            anyLong())).thenThrow(EmptyResultDataAccessException.class);
        assertEquals(Optional.empty(), savedItemDao.findSavedItemByItemIdAndWarehouseId(itemId, warehouseId));
    }

    @Test
    void findSavedItemByItemIdAndWarehouseId_omittedFlowCRUDException(){
        Long itemId = 18L;
        Long warehouseId = 40L;
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<SavedItemRowMapper>any(), anyLong(),
            anyLong())).thenThrow(new DataAccessException(""){});
        CRUDException exception = assertThrows(CRUDException.class,()->
            savedItemDao.findSavedItemByItemIdAndWarehouseId(itemId, warehouseId));
        assertEquals("Failed during `select` {itemId = " + itemId + " warehouse_id = " + warehouseId + "}", exception.getMessage());
    }

    @Test
    void hardDelete_successFlow(){
        Long accountId = 2L;
        when(jdbcTemplate.update(anyString(), anyLong())).thenReturn(1);
        savedItemDao.hardDelete(accountId);
        verify(jdbcTemplate, times(1)).update(anyString(), anyLong());
    }

    @Test
    void hardDelete_omittedFlowCRUDException(){
        Long accountId = 2L;
        when(jdbcTemplate.update(anyString(), anyLong())).thenThrow(new DataAccessException(""){});

        CRUDException exception = assertThrows(CRUDException.class,()->savedItemDao.hardDelete(accountId));
        assertEquals("Error during hard `delete` saved item {accountId = " + accountId + "}", exception.getMessage());
    }
}

