package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.dao.impl.EventDaoImpl;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.EventType;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.mapper.jdbc.EventRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<EventRowMapper> captor2 = ArgumentCaptor.forClass(EventRowMapper.class);
        ArgumentCaptor<Integer> captor3 = ArgumentCaptor.forClass(Integer.class);

        Mockito.doReturn(new ArrayList<Event>()).when(jdbcTemplate).query(captor.capture(), captor2.capture());
        Mockito.doReturn(5).when(jdbcTemplate).queryForObject(captor.capture(), captor3.capture());
        eventDao.findAll(pageable, params, user);
        System.out.println("***\n\n\n\n" + captor.getAllValues() + "\n\n\n\n***");
    }

}
