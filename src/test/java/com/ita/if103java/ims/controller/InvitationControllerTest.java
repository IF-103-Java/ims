package com.ita.if103java.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ita.if103java.ims.dto.AccountDto;
import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.AccountService;
import com.ita.if103java.ims.service.InvitationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InvitationControllerTest {

    private MockMvc mockMvc;

    @Mock
    InvitationService invitationService;

    @InjectMocks
    AccountController accountController;

    private User user;
    private AccountType accountType;
    private UserDto userDto;
    private AccountDto accountDto;
    private UserDetailsImpl userDetails;
    private ZonedDateTime currentDateTime;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();

        currentDateTime = ZonedDateTime.now(ZoneId.systemDefault());
        user = new User(1L, "First name", "Last name", "im.user@gmail.com","nfdfsasf", Role.ROLE_ADMIN,
            currentDateTime, currentDateTime,  true, "rddfgfd", 3L);
        accountType = new AccountType(2L, "Premium", 300.0, 2,
            100, 100, 100, 100, 100,
            true, true, true);
        userDetails = new UserDetailsImpl(user, accountType);
        accountDto = new AccountDto(3L, "Name", 2L, true);

        userDto = new UserDto();
        userDto.setFirstName("First Name");
        userDto.setLastName("Last Name");
        userDto.setEmail("im.user@gmail.com");

    }

    @Test
    void invite() throws Exception {
        // invitationService.inviteUser(user.getUser(), userDto);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(post("/invite/")
            .principal(new UsernamePasswordAuthenticationToken(userDetails, userDetails.getAuthorities()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(resultJson))
            .andExpect(status().isOk());
    }
}
