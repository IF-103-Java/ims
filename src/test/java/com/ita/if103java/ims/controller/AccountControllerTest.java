package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.AccountDto;
import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.handler.GlobalExceptionHandler;
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

import java.util.Collections;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();

        user = new User();
        user.setId(1L);
        user.setAccountId(2L);
    }

    @Test
    void updateSuccess() throws Exception {
        String newName = "New Name";
        AccountDto accountDto = new AccountDto(1L, newName, 1L, true);
        //return accountService.update(user.getUser(), name);
        when(accountService.update(user, newName)).thenReturn(accountDto);
        mockMvc.perform(get("/accounts/" + newName)
            .contentType(MediaType.APPLICATION_JSON))
           // .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(accountDto.getId()))
            .andExpect(jsonPath("$.name").value(accountDto.getName()))
            .andExpect(jsonPath("$.typeId").value(accountDto.getTypeId()))
            .andExpect(jsonPath("$.active").value(accountDto.isActive()));
    }

    @Test
    void view() {
        // return accountService.view(user.getUser().getAccountId());
    }
}
