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
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.ArgumentMatchers.anyListOf;
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
    private EventDtoMapper eventDtoMapperMock;

    private EventDtoMapper eventDtoMapper;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;


    @InjectMocks
    private EventServiceImpl eventService;

    private Event event;
    private UserDetailsImpl userDetails;
    private User user;
    private Map<String, Object> params;
    private PageRequest pageable;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        eventDtoMapper = new EventDtoMapper();

        event = new Event();
        event.setMessage("New client test");
        event.setId(2l);
        event.setAccountId(2l);
        event.setName(EventName.NEW_CLIENT);
        event.setAuthorId(4l);
        event.setAccountId(2l);

        user = new User();
        user.setAccountId(2l);
        userDetails = new UserDetailsImpl(user);

        params = new HashMap<>();
        params.put("type", new ArrayList<>(Arrays.asList("USER", "WAREHOUSE")));
        params.put("after", "02-02-2002");

        pageable = PageRequest.of(0, 15, Sort.Direction.ASC, "id");

        when(eventDao.create(event)).thenReturn(this.event);
        List<Event> events = Arrays.asList(
            new Event("Test message 1", 2l, 26l, 4l,
                EventName.ITEM_CAME, null),
            new Event("Test message 2", 2l, null, 5l,
                EventName.NEW_CLIENT, null));
        events.forEach(event -> event.setId(1l));
        when(eventDao.findAll(pageable, params, userDetails.getUser()))
            .thenReturn(new PageImpl<Event>(events, pageable, 3));

        when(eventDtoMapperMock.toDtoList(events)).thenReturn(eventDtoMapper.toDtoList(events));

        Map<Long, String> usernames = new HashMap();
        usernames.put(4l, "Paul Mccarartney");
        usernames.put(5l, "John Lenon");
        when(userDao.findUserNamesById(anyListOf(Long.class))).thenReturn(usernames);

        Map<Long, String> warehouses = new HashMap();
        warehouses.put(26l, "Warehouse A");
        when(warehouseDao.findWarehouseNamesById(anyListOf(Long.class))).thenReturn(warehouses);


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
        Page<EventDto> result = eventService.findAll(pageable, params, userDetails);
        verify(eventDao, times(1)).findAll(pageable, params, userDetails.getUser());
        verify(userDao, atMostOnce()).findUserNamesById(anyListOf(Long.class));
        verify(warehouseDao, atMostOnce()).findWarehouseNamesById(anyListOf(Long.class));
        assertNotNull(result.getContent().get(0).getAuthor());
    }

    @Test
    public void testDeleteByAccountId() {
        eventService.deleteByAccountId(userDetails.getUser().getAccountId());
        verify(eventDao, times(1)).deleteByAccountId(userDetails.getUser().getAccountId());
    }
}
