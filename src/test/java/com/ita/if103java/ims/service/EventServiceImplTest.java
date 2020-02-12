package com.ita.if103java.ims.service;


import com.ita.if103java.ims.dao.EventDao;
import com.ita.if103java.ims.dao.UserDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.mapper.dto.EventDtoMapper;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.impl.EventServiceImpl;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class EventServiceImplTest {

    @Mock
    private EventDao eventDao;

    @Mock
    private UserDao userDao;

    @Mock
    private WarehouseDao warehouseDao;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private EventDtoMapper eventDtoMapper;

    @InjectMocks
    private EventServiceImpl eventService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateEvent() {
        Event event = new Event("Test message", 2l, null, 4l, EventName.NEW_CLIENT, null);
        doReturn(event).when(eventDao).create(event);
        eventService.create(event);
        verify(eventDao, times(1)).create(event);
        verify(simpMessagingTemplate, atMostOnce()).convertAndSend("/topic/events/" + event.getAccountId(), eventDtoMapper.toDto(event));
    }

    @Test
    public void testFindAllEvents() {
        List<Event> eventListFromDao = Arrays.asList(
            new Event("Test message1", 2l, 26l, 4l, EventName.ITEM_CAME, null),
            new Event("Test message2", 2l, null, 5l, EventName.NEW_CLIENT, null)
        );

        Map<Long, String> usernames = new HashMap();
        usernames.put(4l, "Vasya Pupkin");
        usernames.put(5l, "John Lenon");

        Map<Long, String> warehouses = new HashMap();
        usernames.put(26l, "Warehouse A");

        Pageable pageable = PageRequest.of(0, 15, Sort.Direction.ASC, "id");
        Map<String, Object> params = new HashMap<>();
        UserDetailsImpl userDetails = new UserDetailsImpl(new User());

        doReturn(new PageImpl<Event>(eventListFromDao, pageable, 3)).when(eventDao).findAll(pageable, params, userDetails.getUser());
        doReturn(usernames).when(userDao).findUserNamesById(Arrays.asList(4l, 5l));
        doReturn(warehouses).when(warehouseDao).findWarehouseNamesById(Arrays.asList(26l));

        assertTrue(eventService.findAll(pageable, params, userDetails) instanceof Page);
        verify(eventDao, times(1)).findAll(pageable, params, userDetails.getUser());
        verify(userDao, atMostOnce()).findUserNamesById(Arrays.asList(4l, 5l));
        verify(warehouseDao, atMostOnce()).findWarehouseNamesById(Arrays.asList(26l));
    }
}
