package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.AccountTypeDto;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.AccountService;
import com.ita.if103java.ims.service.UpgradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UpgradeControllerTest {


    private MockMvc mockMvc;

    @Mock
    UpgradeService upgradeService;

    @InjectMocks
    UpgradeController upgradeController;

    private Long accountTypeId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(upgradeController).build();

        accountTypeId = 2L;
    }

    @Test
    void upgrade() throws Exception {
        mockMvc.perform(put("/upgrade/" + accountTypeId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(upgradeService).upgradeAccount(any(UserDetailsImpl.class), eq(accountTypeId));
    }

    @Test
    void findCurrentType() throws Exception {
        AccountTypeDto accountTypeDto = new AccountTypeDto();
        when(upgradeService.findById(any())).thenReturn(accountTypeDto);
        mockMvc.perform(get("/upgrade/")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void findAllPossible() {
    }
}
