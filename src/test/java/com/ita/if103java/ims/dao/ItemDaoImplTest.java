package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.config.GeneratedKeyHolderFactory;
import com.ita.if103java.ims.dao.impl.ItemDaoImpl;
import com.ita.if103java.ims.entity.Item;
import com.ita.if103java.ims.exception.dao.CRUDException;
import com.ita.if103java.ims.exception.dao.ItemNotFoundException;
import com.ita.if103java.ims.mapper.jdbc.ItemRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@SpringBootTest
public class ItemDaoImplTest {
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
    private ItemDaoImpl itemDaoImpl;

    private Item item;
    private List<Item> items;
    private final Long accountId = 2L;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.initMocks(this);
        when(this.jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(this.dataSource.getConnection()).thenReturn(this.connection);
        when(this.connection.prepareStatement(anyString(), anyInt())).thenReturn(this.preparedStatement);
        when(this.generatedKeyHolderFactory.newKeyHolder()).thenReturn(keyHolder);
        when(this.keyHolder.getKey()).thenReturn(1L);
        when(this.jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenReturn(1);

        item = new Item();
        item.setName("Green-Apple");
        item.setAccountId(accountId);
        item.setDescription("Sweet apple");
        item.setUnit("box");
        item.setVolume(5);

        items = new ArrayList<>();
        Item trout = new Item();
        trout.setName("Fish-Trout");
        trout.setDescription("fresh trout");
        trout.setAccountId(accountId);
        trout.setUnit("box");
        trout.setActive(true);
        trout.setVolume(4);

        items.add(trout);

        Item salmon = new Item();
        salmon.setName("Fish-Salmon");
        salmon.setDescription("fresh salmon");
        salmon.setAccountId(1L);
        salmon.setUnit("box");
        trout.setActive(true);
        salmon.setVolume(5);

        items.add(salmon);

        Item catfish = new Item();
        catfish.setName("Catfish");
        catfish.setDescription("fresh salmon");
        catfish.setAccountId(3L);
        catfish.setUnit("box");
        catfish.setVolume(5);
        trout.setActive(true);

        items.add(catfish);
    }

    @Test
    public void testCreate_successFlow(){
        assertEquals(item, itemDaoImpl.addItem(item));
        assertNotNull(item.getId());
    }

    @Test
      void testCreate_omittedNotNullFields(){
        when(this.keyHolder.getKey()).thenReturn(null);
        item = new Item();
        assertThrows(CRUDException.class, () ->itemDaoImpl.addItem(item));
    }

    @Test
      void testGetItems_successFlow() {
        for (Item item: items) {
            itemDaoImpl.addItem(item);
        }
        List<Item> newItems = new ArrayList<>();
        newItems.add(items.get(0));
        PageRequest pageable = PageRequest.of(0, 3, Sort.Direction.ASC, "id");
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<ItemRowMapper>any(), eq(accountId), eq(pageable.getPageSize()),
            eq(pageable.getOffset())))
            .thenReturn(newItems);
        Integer expectedCount = 1;
       List<Item> resultList = itemDaoImpl.getItems(accountId, pageable.getPageSize(), pageable.getOffset(),
           pageable.getSort());
       assertEquals(expectedCount, resultList.size());
    }

    @Test
    public void testCountItemsById_successFlow() {
        Integer count = 3;
        when(this.jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyLong()))
            .thenReturn(count);

        assertEquals(count, itemDaoImpl.countItemsById(accountId));
    }

    @Test
    public void testFindItemByName_successFlow() {
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<ItemRowMapper>any(), anyLong(),
            anyString()))
            .thenReturn(item);

        assertEquals(item, itemDaoImpl.findItemByName(item.getName(), accountId));
    }

    @Test
    public void testFindItemByAccountId_successFlow() {
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<ItemRowMapper>any(), anyLong()))
            .thenReturn(items);
        assertEquals(items, itemDaoImpl.findItemByAccountId(accountId));
    }

    @Test
    void findItemById(){
        Long itemId = 2L;
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<ItemRowMapper>any(), anyLong(),
            anyLong())).thenReturn(item);
        assertEquals(item, itemDaoImpl.findItemById(itemId, accountId));
    }

    @Test
    void findItemsById(){
        String ids = "1, 4, 8";
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<ItemRowMapper>any(), anyLong()))
            .thenReturn(items);
        assertEquals(items, itemDaoImpl.findItemsById(ids, accountId));
    }

    @Test
    void isExistItemById(){
        Long itemId = 2L;
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<Boolean>>any(), anyLong(),
            anyLong())).thenReturn(true);
        assertEquals(true, itemDaoImpl.isExistItemById(itemId, accountId));
    }

    @Test
    void softDeleteItem_successFlow(){
        Long itemId = 2L;
        when(jdbcTemplate.update(anyString(), eq(false), anyLong(),
            anyLong())).thenReturn(1);
        assertEquals(true, itemDaoImpl.softDeleteItem(itemId, accountId));
    }

    @Test
    void softDeleteItem_omittedFlow(){
        Long itemId = 2L;
        when(jdbcTemplate.update(anyString(), eq(false), anyLong(),
            anyLong())).thenReturn(0);
        assertThrows(ItemNotFoundException.class,
            ()->itemDaoImpl.softDeleteItem(itemId,  accountId));
    }

    @Test
    void hardDeleteItem_successFlow(){
        when(jdbcTemplate.update(anyString(), anyLong())).thenReturn(1);
        itemDaoImpl.hardDelete(accountId);
        verify(jdbcTemplate, times(1)).update(anyString(), eq(accountId));
    }

    @Test
    void findItemsByNameQuery() {
        String query = "Fish";
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<ItemRowMapper>any(), anyString(), anyLong()))
            .thenReturn(items);
        assertEquals(items, itemDaoImpl.findItemsByNameQuery(query, accountId));
    }

    @Test
    void updateItem_successFlow(){
        item.setId(1L);
        when(jdbcTemplate.update(anyString(), anyString(), anyString(), anyString(), anyInt(), anyLong(),
            anyLong())).thenReturn(1);
        assertEquals(item, itemDaoImpl.updateItem(item));
    }

    @Test
    void updateItem_omittedFlow(){
        item.setId(1L);
        when(jdbcTemplate.update(anyString(), anyString(), anyString(), anyString(), anyInt(), anyLong(),
            anyLong())).thenReturn(0);
        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,()-> itemDaoImpl.updateItem(item));
        assertEquals("Failed to get savedItem during `update` {id" + item.getId() + "}", exception.getMessage());
    }
}
