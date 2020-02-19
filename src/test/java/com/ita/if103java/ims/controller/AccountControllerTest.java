package com.ita.if103java.ims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ita.if103java.ims.dto.AccountDto;
import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.exception.dao.AccountNotFoundException;
import com.ita.if103java.ims.handler.GlobalExceptionHandler;
import com.ita.if103java.ims.security.SecurityInterceptor;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;

import static com.ita.if103java.ims.security.SecurityInterceptor.init;
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
    private ZonedDateTime currentDateTime;
    private AccountNotFoundException accountNotFoundException;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController)
            .setControllerAdvice(GlobalExceptionHandler.class)
            .addInterceptors(new SecurityInterceptor())
            .setCustomArgumentResolvers(
                new AuthenticationPrincipalArgumentResolver())
            .build();

        currentDateTime = ZonedDateTime.now(ZoneId.systemDefault());
        user = new User(1L, "First name", "Last name", "im.user@gmail.com","nfdfsasf", Role.ROLE_ADMIN,
            currentDateTime, currentDateTime,  true, "rddfgfd", 3L);
        accountType = new AccountType(2L, "Premium", 300.0, 2,
            100, 100, 100, 100, 100,
            true, true, true);
        userDetails = new UserDetailsImpl(user, accountType);
        accountDto = new AccountDto(3L, "Name", 2L, true);

        accountNotFoundException = new AccountNotFoundException("Account not found");
    }

    @Test
    void update_SuccessFlow() throws Exception {
        when(accountService.update(user, accountDto.getName())).thenReturn(accountDto);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String resultJson = accountDto.getName();

        mockMvc.perform(put("/accounts/")
            .principal(init(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(resultJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(accountDto.getId()))
            .andExpect(jsonPath("$.name").value(accountDto.getName()))
            .andExpect(jsonPath("$.typeId").value(accountDto.getTypeId()))
            .andExpect(jsonPath("$.active").value(accountDto.isActive()));

        verify(accountService).update(user, accountDto.getName());
    }

    @Test
    void update_AccountNotFoundException() throws Exception {
        when(accountService.update(any(User.class), anyString())).thenThrow(accountNotFoundException);

        mockMvc.perform(put("/accounts/")
            .principal(init(userDetails))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void view_SuccessFlow() throws Exception {
        when(accountService.view(userDetails.getUser().getAccountId())).thenReturn(accountDto);
        mockMvc.perform(get("/accounts/")
            .principal(init(userDetails))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(accountDto.getId()))
            .andExpect(jsonPath("$.name").value(accountDto.getName()))
            .andExpect(jsonPath("$.typeId").value(accountDto.getTypeId()))
            .andExpect(jsonPath("$.active").value(accountDto.isActive()));

        verify(accountService).view(userDetails.getUser().getAccountId());
    }
}
