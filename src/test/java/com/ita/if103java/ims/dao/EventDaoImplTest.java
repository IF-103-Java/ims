package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.config.GeneratedKeyHolderFactory;
import com.ita.if103java.ims.dao.impl.EventDaoImpl;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.exception.dao.CRUDException;
import com.ita.if103java.ims.mapper.jdbc.EventRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

public class EventDaoImplTest {

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
    private EventDaoImpl eventDao;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    private User user;
    private Event event;
    private ZonedDateTime currentDateTime = ZonedDateTime.now();
    private Map<String, Object> params;
    private PageRequest pageable;


    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.initMocks(this);
        event = new Event();
        event.setDate(currentDateTime);
        event.setName(EventName.NEW_CLIENT);
        event.setAuthorId(4l);
        event.setMessage("New client test");
        event.setAccountId(2l);

        user = new User();
        user.setRole(Role.ROLE_ADMIN);
        user.setAccountId(2l);
        user.setId(4l);

        params = new HashMap<>();
        params.put("type", new ArrayList<>(Arrays.asList("USER", "WAREHOUSE")));
        params.put("after", "02-02-2002");

        pageable = PageRequest.of(0, 15, Sort.Direction.ASC, "id");

        when(this.keyHolder.getKey()).thenReturn(1L);
        when(this.jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenReturn(1);
        when(this.jdbcTemplate.update(anyString(), anyLong())).thenReturn(1);
        when(this.generatedKeyHolderFactory.newKeyHolder()).thenReturn(keyHolder);
        when(this.dataSource.getConnection()).thenReturn(this.connection);
        when(this.generatedKeyHolderFactory.newKeyHolder()).thenReturn(keyHolder);
        when(this.connection.prepareStatement(anyString(), anyInt())).thenReturn(this.preparedStatement);
        when(jdbcTemplate.query(stringArgumentCaptor.capture(),
            ArgumentMatchers.<EventRowMapper>any())).thenReturn(new ArrayList<Event>());
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(5);
    }

    @Test
    public void testCreate_successFlow() {
        assertEquals(event, eventDao.create(event));
        assertNotNull(user.getId());
    }

    @Test
    void testCreate_omittedNotNullFields() {
        when(this.keyHolder.getKey()).thenReturn(null);
        event = new Event();
        assertThrows(CRUDException.class, () -> eventDao.create(event));
    }

    @Test
    public void testFindAllForWorker_successFlow() {
        user.setRole(Role.ROLE_WORKER);
        String expectedQuery = """
            select * from events where account_id = '2' and DATE(date) >= '02-02-2002' and (name in
            ('ITEM_ENDED', 'LOW_SPACE_IN_WAREHOUSE', 'WAREHOUSE_CREATED', 'WAREHOUSE_EDITED',
            'WAREHOUSE_REMOVED') or (name in ('LOGIN', 'LOGOUT', 'PASSWORD_CHANGED', 'PROFILE_CHANGED', 'SIGN_UP')
            and author_id = '4')) ORDER BY id ASC Limit 15 OFFSET 0
        """.replace("\n", " ").replaceAll("\\s{2,}", " ").trim();
        eventDao.findAll(pageable, params, user);
        assertEquals(expectedQuery,
            stringArgumentCaptor.getValue().replaceAll("\\s{2,}", " ").trim());
    }


    @Test
    public void testFindAllForAdmin_successFlow() {
        String expectedQuery = """
            select * from events where account_id = '2' and DATE(date) >= '02-02-2002' and
            name in ('ITEM_ENDED', 'LOGIN', 'LOGOUT', 'LOW_SPACE_IN_WAREHOUSE', 'PASSWORD_CHANGED', 'PROFILE_CHANGED',
            'SIGN_UP', 'WAREHOUSE_CREATED', 'WAREHOUSE_EDITED', 'WAREHOUSE_REMOVED') ORDER BY id ASC Limit 15 OFFSET 0
        """.replace("\n", " ").replaceAll("\\s{2,}", " ").trim();
        eventDao.findAll(pageable, params, user);
        assertEquals(expectedQuery, stringArgumentCaptor.getValue().replaceAll("\\s{2,}", " ").trim());
    }

    @Test
    public void testDeleteByAccountId() {
        eventDao.deleteByAccountId(user.getAccountId());
        verify(jdbcTemplate, times(1)).update(anyString(), anyLong());
    }

}