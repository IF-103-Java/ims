package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.dao.impl.EventDaoImpl;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.mapper.jdbc.EventRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

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

    @Captor
    private ArgumentCaptor<EventRowMapper> rowMapperArgumentCaptor;

    @Captor
    private ArgumentCaptor<Class> classArgumentCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
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

        Mockito.doReturn(new ArrayList<Event>()).when(jdbcTemplate).query(stringArgumentCaptor.capture(), rowMapperArgumentCaptor.capture());
        Mockito.doReturn(5).when(jdbcTemplate).queryForObject(stringArgumentCaptor.capture(), classArgumentCaptor.capture());
        eventDao.findAll(pageable, params, user);
        System.out.println("***\n\n\n\n" + stringArgumentCaptor.getValue() + "\n\n\n\n***");
    }

}
