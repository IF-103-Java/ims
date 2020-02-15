package com.ita.if103java.ims.service;


import com.ita.if103java.ims.dao.EventDao;
import com.ita.if103java.ims.dao.UserDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dto.EventDto;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.mapper.dto.EventDtoMapper;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventServiceImplTest {

    @Mock
    private EventDao eventDao;

    @Mock
    private UserDao userDao;

    @Mock
    private WarehouseDao warehouseDao;

    @Mock
    EventDtoMapper eventDtoMapper;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;


    @InjectMocks
    private EventServiceImpl eventService;

    private Event event;
    private UserDetailsImpl userDetails;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        // Initializing test event
        event = new Event();
        event.setMessage("New client test");
        event.setId(2l);
        event.setAccountId(2l);
        event.setName(EventName.NEW_CLIENT);
        event.setAuthorId(4l);
        event.setAccountId(2l);

        // Initializing test user
        userDetails = new UserDetailsImpl(new User());

        when(eventDao.create(event)).thenReturn(event);
    }

    @Test
    public void testCreateEvent() {
        eventService.create(event);
        verify(eventDao, times(1)).create(event);
        verify(simpMessagingTemplate, atMostOnce()).convertAndSend(anyString(), ArgumentMatchers.<EventDto>any());
        assertNotNull(event.getDate());
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

        when(eventDao.findAll(pageable, params, userDetails.getUser())).thenReturn(new PageImpl<Event>(eventListFromDao, pageable, 3));
        when(userDao.findUserNamesById(Arrays.asList(4l, 5l))).thenReturn(usernames);
        when(warehouseDao.findWarehouseNamesById(Arrays.asList(26l))).thenReturn(warehouses);

        assertTrue(eventService.findAll(pageable, params, userDetails) instanceof Page);
        verify(eventDao, times(1)).findAll(pageable, params, userDetails.getUser());
        verify(userDao, atMostOnce()).findUserNamesById(Arrays.asList(4l, 5l));
        verify(warehouseDao, atMostOnce()).findWarehouseNamesById(Arrays.asList(26l));
    }

    @Test
    public void testDeleteByAccountId() {
        long accountId = 2;
        eventService.deleteByAccountId(accountId);
        verify(eventDao, times(1)).deleteByAccountId(accountId);
    }
}
