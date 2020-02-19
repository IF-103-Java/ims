package com.ita.if103java.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ita.if103java.ims.IgnoreJacksonWriteOnlyAccess;
import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.handler.GlobalExceptionHandler;
import com.ita.if103java.ims.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RegistrationControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private RegistrationController registrationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UserDto createdUserDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
            .standaloneSetup(registrationController)
            .setControllerAdvice(GlobalExceptionHandler.class)
            .build();

        //Initialization json mapper
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setAnnotationIntrospector(new IgnoreJacksonWriteOnlyAccess());

        //Initializing created user
        createdUserDto = new UserDto();
        createdUserDto.setFirstName("Mary");
        createdUserDto.setLastName("Smith");
        createdUserDto.setEmail("mary.smith@gmail.com");
        createdUserDto.setPassword("qwerty123");
        createdUserDto.setAccountName("Reactor");

    }


    @Test
    void create_successFlow() throws Exception {
        when(userService.createAndSendMessage(any(UserDto.class))).thenReturn(createdUserDto);

        // Converting DTO object to json
        String json = objectMapper.writeValueAsString(createdUserDto);

        mockMvc.perform(post("/sign-up")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isCreated());
    }
}
