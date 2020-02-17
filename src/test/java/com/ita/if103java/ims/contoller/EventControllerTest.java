package com.ita.if103java.ims.contoller;


import com.ita.if103java.ims.controller.EventController;
import com.ita.if103java.ims.dto.EventDto;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    private UserDetailsImpl userDetails;
    private Pageable pageable;
    private Map<String, Object> params;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        userDetails = new UserDetailsImpl();
        pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "id");
        params = new HashMap();

        when(eventService.findAll(pageable, params, userDetails)).thenReturn(new PageImpl<EventDto>(new ArrayList<>()));
    }

    @Test
    public void testFindAll() {
        assertTrue(eventController.findAll(pageable, params, userDetails).getContent().isEmpty());
        verify(eventService, times(1)).findAll(pageable, params, userDetails);
    }
}
