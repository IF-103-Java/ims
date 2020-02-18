package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.config.GeneratedKeyHolderFactory;
import com.ita.if103java.ims.dao.impl.ItemDaoImpl;
import com.ita.if103java.ims.entity.Item;
import com.ita.if103java.ims.exception.dao.CRUDException;
import com.ita.if103java.ims.mapper.jdbc.ItemRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

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
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    ItemRowMapper itemRowMapper;


    @InjectMocks
    private ItemDaoImpl itemDaoImpl;

    private Item item;
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

        // Initializing test Item
        item = new Item();
        item.setName("Green-Apple");
        item.setAccountId(accountId);
        item.setDescription("Sweet apple");
        item.setUnit("box");
        item.setVolume(5);
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
        List<Item> items = this.getListOfItems();
        for (Item item: items) {
            itemDaoImpl.addItem(item);
        }
        List<Item> newItems = new ArrayList<>();
        newItems.add(items.get(0));
        PageRequest pageable = PageRequest.of(0, 3, Sort.Direction.ASC, "id");
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<ItemRowMapper>any(), eq(accountId), eq(pageable.getPageSize()),
            eq(pageable.getOffset())))
            .thenReturn(newItems);
        // First 3 items (should return 1, because there is only 1 item with accountId 2L)
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
   private List<Item> getListOfItems(){
       List<Item> items = new ArrayList<>();
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

       return items;
   }


}
