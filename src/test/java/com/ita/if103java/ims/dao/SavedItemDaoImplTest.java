package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.config.GeneratedKeyHolderFactory;
import com.ita.if103java.ims.dao.impl.SavedItemDaoImpl;
import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.mapper.jdbc.SavedItemRowMapper;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
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
    void getSavedItems(){
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<SavedItemRowMapper>any())).thenReturn(savedItems);
        assertEquals(savedItems, savedItemDao.getSavedItems());
    }

    @Test
    void findSavedItemById(){
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<SavedItemRowMapper>any(), anyLong())).thenReturn(trout);
        assertEquals(trout, savedItemDao.findSavedItemById(trout.getId()));
    }

    @Test
    void findSavedItemByItemId(){
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<SavedItemRowMapper>any(), anyLong())).thenReturn(savedItems);
        assertEquals(savedItems, savedItemDao.findSavedItemByItemId(trout.getItemId()));
    }

    @Test
    void findSavedItemByWarehouseId(){
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<SavedItemRowMapper>any(), anyLong())).thenReturn(savedItems);
        assertEquals(savedItems, savedItemDao.findSavedItemByWarehouseId(trout.getWarehouseId()));
    }

    @Test
    void existSavedItemByWarehouseId(){
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<SavedItemRowMapper>any(), anyLong())).thenReturn(savedItems);
        assertEquals(true, savedItemDao.existSavedItemByWarehouseId(trout.getWarehouseId()));
        verify(jdbcTemplate, times(1)).query(anyString(), ArgumentMatchers.<SavedItemRowMapper>any(), anyLong());
    }
}

