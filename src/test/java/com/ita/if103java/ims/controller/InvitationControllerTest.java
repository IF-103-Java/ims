package com.ita.if103java.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ita.if103java.ims.dto.AccountDto;
import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.exception.service.UserLimitReachedException;
import com.ita.if103java.ims.handler.GlobalExceptionHandler;
import com.ita.if103java.ims.security.SecurityInterceptor;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.InvitationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.ita.if103java.ims.security.SecurityInterceptor.init;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InvitationControllerTest {

    private MockMvc mockMvc;

    @Mock
    InvitationService invitationService;

    @InjectMocks
    InvitationController invitationController;

    private User user;
    private AccountType accountType;
    private UserDto userDto;
    private AccountDto accountDto;
    private UserDetailsImpl userDetails;
    private ZonedDateTime currentDateTime;
    private UserLimitReachedException userLimitReachedException;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(invitationController)
            .setControllerAdvice(GlobalExceptionHandler.class)
            .addInterceptors(new SecurityInterceptor())
            .setCustomArgumentResolvers(
                new AuthenticationPrincipalArgumentResolver())
            .build();

        currentDateTime = ZonedDateTime.now(ZoneId.systemDefault());
        user = new User(1L, "First name", "Last name", "im.user@gmail.com", "nfdfsasf", Role.ROLE_ADMIN,
            currentDateTime, currentDateTime, true, "rddfgfd", 3L);
        accountType = new AccountType(2L, "Premium", 300.0, 2,
            100, 100, 100, 100, 100,
            true, true, true);
        userDetails = new UserDetailsImpl(user, accountType);
        accountDto = new AccountDto(3L, "Name", 2L, true);

        userDto = new UserDto();
        userDto.setFirstName("First Name");
        userDto.setLastName("Last Name");
        userDto.setEmail("im.user@gmail.com");

        userLimitReachedException = new UserLimitReachedException("User limit reached");
    }

    @Test
    void invite_SuccessFlow() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(post("/invite/")
            .principal(init(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(resultJson))
            .andExpect(status().isOk());

        verify(invitationService).inviteUser(userDetails.getUser(), userDto);
    }

    @Test
    void invite_UserLimitReachedException() throws Exception {
        doThrow(userLimitReachedException).when(invitationService).inviteUser(userDetails.getUser(), userDto);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(post("/invite/")
            .principal(init(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(resultJson))
            .andExpect(status().isOk());

        verify(invitationService).inviteUser(userDetails.getUser(), userDto);
    }
}
