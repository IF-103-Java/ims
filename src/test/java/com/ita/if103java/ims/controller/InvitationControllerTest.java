package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.AccountDto;
import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.service.AccountService;
import com.ita.if103java.ims.service.InvitationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
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
    private UserDto userDto;
    private AccountDto accountDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();

        user = new User();
        user.setId(1L);
        user.setAccountId(2L);

        userDto = new UserDto();
        userDto.setFirstName("First Name");
        userDto.setLastName("Last Name");
        userDto.setEmail("im.user@gmail.com");

        accountDto = new AccountDto(1L, "Name", 1L, true);

    }

    @Test
    void invite() throws Exception {
        // invitationService.inviteUser(user.getUser(), userDto);

        mockMvc.perform(post("/invite/")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
