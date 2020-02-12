package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.dao.impl.EventDaoImpl;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.mapper.jdbc.EventRowMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    @Test
    public void testCreate() {
        Event event = new Event("Test message", 2l, null, 4l, EventName.NEW_CLIENT, null);
        event.setDate(ZonedDateTime.now());
//        Mockito.when(jdbcTemplate.update()).thenReturn(1);
//        eventDao.create()

    }
    @Test
    public void testFindAll() {
        PageRequest pageable = PageRequest.of(0, 15, Sort.Direction.ASC, "id");
        Map<String, Object> params = new HashMap<>();
        params.put("type", "USER");
        User user = new User();
        user.setAccountId(2l);
        user.setRole(Role.ROLE_WORKER);
        user.setId(5l);
        String expectedQuery = "select * from events where account_id = '2' and (name in ('LOGIN', 'LOGOUT', 'PROFILE_CHANGED', 'SIGN_UP', 'PASSWORD_CHANGED') and author_id = '5') ORDER BY id ASC Limit 15 OFFSET 0\n";
        Mockito.when(jdbcTemplate.query(stringArgumentCaptor.capture(), ArgumentMatchers.<EventRowMapper>any())).thenReturn(new ArrayList<Event>());
        Mockito.doReturn(5).when(jdbcTemplate).queryForObject(stringArgumentCaptor.capture(), Mockito.eq(Integer.class));
        eventDao.findAll(pageable, params, user);
        Assertions.assertEquals(expectedQuery, stringArgumentCaptor.getAllValues().get(0));
    }

    @Test
    public void testDeleteByAccountId() {
        long accountId = 2;
        eventDao.deleteByAccountId(accountId);
        Mockito.verify(jdbcTemplate, Mockito.times(1)).update(Mockito.anyString(), Mockito.anyLong());
    }

}
