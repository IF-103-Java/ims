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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();

        user = new User();
        user.setId(1L);
        user.setFirstName("First name");
        user.setLastName("Last name");
        user.setPassword("nfdfsasf");
        user.setEmail("im.user@gmail.com");
        user.setEmailUUID("rddfgfd");
        user.setUpdatedDate(ZonedDateTime.now(ZoneId.systemDefault()));
        user.setUpdatedDate(ZonedDateTime.now(ZoneId.systemDefault()));
        user.setRole(Role.ROLE_ADMIN);
        user.setActive(true);
        user.setAccountId(2L);
        accountType = new AccountType();
        accountType.setId(1L);
        accountType.setName("Basic");
        accountType.setLevel(1);
        accountType.setActive(true);
        userDetails = new UserDetailsImpl(user, accountType);

        userDto = new UserDto();
        userDto.setFirstName("First Name");
        userDto.setLastName("Last Name");
        userDto.setEmail("im.user@gmail.com");

        accountDto = new AccountDto(1L, "Name", 1L, true);

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
