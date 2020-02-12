package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.dao.impl.EventDaoImpl;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.mapper.jdbc.EventRowMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class EventDaoImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;


    @InjectMocks
    private EventDaoImpl eventDao;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Ignore
    @Test
    public void testCreate() {
        Event event = new Event("Test message", 2l, null, 4l, EventName.NEW_CLIENT, null);
        event.setDate(ZonedDateTime.now());
    }

    @Test
    public void testFindAll() {
        PageRequest pageable = PageRequest.of(0, 15, Sort.Direction.ASC, "id");
        Map<String, Object> params = new HashMap<>();
        User user = new User();
        user.setAccountId(2l);
        user.setRole(Role.ROLE_WORKER);
        user.setId(5l);
        params.put("type", new ArrayList<>(Arrays.asList("USER", "WAREHOUSE")));
        String expectedQuery = """
        select * from events where account_id = '2' and (name in
         ('ITEM_ENDED', 'LOW_SPACE_IN_WAREHOUSE', 'WAREHOUSE_CREATED', 'WAREHOUSE_EDITED',
         'WAREHOUSE_REMOVED') or (name in ('LOGIN', 'LOGOUT', 'PASSWORD_CHANGED', 'PROFILE_CHANGED', 'SIGN_UP')
         and author_id = '5')) ORDER BY id ASC Limit 15 OFFSET 0
        """.replace("\n", " ").replaceAll("\\s{2,}", " ").trim();
        when(jdbcTemplate.query(stringArgumentCaptor.capture(), ArgumentMatchers.<EventRowMapper>any())).thenReturn(new ArrayList<Event>());
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(5);
        eventDao.findAll(pageable, params, user);
        assertEquals(expectedQuery,
            stringArgumentCaptor.getValue().replaceAll("\\s{2,}", " ").trim());
    }

    @Test
    public void testFindAll2() {
        PageRequest pageable = PageRequest.of(0, 15, Sort.Direction.ASC, "id");
        Map<String, Object> params = new HashMap<>();
        User user = new User();
        user.setAccountId(2l);
        user.setRole(Role.ROLE_ADMIN);
        user.setId(5l);
        params.clear();
        params.put("after", "20-07-2019");
        params.put("warehouse_id", 12);
        String expectedQuery = """
        select * from events where account_id = '2' and warehouse_id = '12'
        and DATE(date) >= '20-07-2019' ORDER BY id ASC Limit 15 OFFSET 0
        """.replace("\n", " ").replaceAll("\\s{2,}", " ").trim();
        when(jdbcTemplate.query(stringArgumentCaptor.capture(), ArgumentMatchers.<EventRowMapper>any())).thenReturn(new ArrayList<Event>());
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(5);
        eventDao.findAll(pageable, params, user);
        assertEquals(expectedQuery, stringArgumentCaptor.getValue().replaceAll("\\s{2,}", " ").trim());
    }

    @Test
    public void testFindAll3() {
        PageRequest pageable = PageRequest.of(0, 15, Sort.Direction.ASC, "id");
        Map<String, Object> params = new HashMap<>();
        User user = new User();
        user.setAccountId(2l);
        user.setRole(Role.ROLE_ADMIN);
        user.setId(5l);
        params.put("type", new ArrayList<>(Arrays.asList("USER", "WAREHOUSE")));
        String expectedQuery = """
        select * from events where account_id = '2' and name in ('ITEM_ENDED', 'LOGIN', 'LOGOUT',
        'LOW_SPACE_IN_WAREHOUSE', 'PASSWORD_CHANGED', 'PROFILE_CHANGED', 'SIGN_UP', 'WAREHOUSE_CREATED',
        'WAREHOUSE_EDITED', 'WAREHOUSE_REMOVED') ORDER BY id ASC Limit 15 OFFSET 0
            """.replace("\n", " ").replaceAll("\\s{2,}", " ").trim();
        when(jdbcTemplate.query(stringArgumentCaptor.capture(), ArgumentMatchers.<EventRowMapper>any())).thenReturn(new ArrayList<Event>());
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(5);
        eventDao.findAll(pageable, params, user);
        assertEquals(expectedQuery, stringArgumentCaptor.getValue().replaceAll("\\s{2,}", " ").trim());
    }

    @Test
    public void testDeleteByAccountId() {
        long accountId = 2;
        eventDao.deleteByAccountId(accountId);
        verify(jdbcTemplate, times(1)).update(anyString(), anyLong());
    }

}
