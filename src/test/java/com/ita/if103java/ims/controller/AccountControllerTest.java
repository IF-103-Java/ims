package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.AccountDto;
import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.handler.GlobalExceptionHandler;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    AccountService accountService;

    @InjectMocks
    AccountController accountController;

    private User user;
    private AccountDto accountDto;
    private UserDetailsImpl userDetails;
    private AccountType accountType;

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

        accountDto = new AccountDto(1L, "Name", 1L, true);

    }

    @Test
    void updateSuccess() throws Exception {
        String newName = "Name";
        when(accountService.update(any(User.class), anyString())).thenReturn(accountDto);
        mockMvc.perform(put("/accounts/")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(accountDto.getId()))
            .andExpect(jsonPath("$.name").value(accountDto.getName()))
            .andExpect(jsonPath("$.typeId").value(accountDto.getTypeId()))
            .andExpect(jsonPath("$.active").value(accountDto.isActive()));
    }

    @Test
    void view() throws Exception {
        when(any(UserDetailsImpl.class).getUser()).thenReturn(user);
        when(accountService.view(anyLong())).thenReturn(accountDto);
        mockMvc.perform(get("/accounts/")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(accountDto.getId()))
            .andExpect(jsonPath("$.name").value(accountDto.getName()))
            .andExpect(jsonPath("$.typeId").value(accountDto.getTypeId()))
            .andExpect(jsonPath("$.active").value(accountDto.isActive()));
    }
}
