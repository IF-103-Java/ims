package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.AccountDto;
import com.ita.if103java.ims.dto.AccountTypeDto;
import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.entity.Role;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
    private User user;
    private AccountDto accountDto;
    private UserDetailsImpl userDetails;
    private AccountType accountType;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(upgradeController).build();

        accountTypeId = 2L;

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
            .principal(new UsernamePasswordAuthenticationToken(userDetails, userDetails.getAuthorities()))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void findAllPossible() throws Exception {
        //return upgradeService.findAllPossibleToUpgrade(user.getAccountType().getLevel());
        List<AccountTypeDto> allPossibleDto = Arrays.asList(
            new AccountTypeDto(1L, "Basic", 0.0, 1, 3, 3,
                3, 3, 3, false,
                false, true),
            new AccountTypeDto(2L, "Premium", 500.0, 2, 100, 100,
                100, 100, 100, true,
                true, true)
        );
        when(upgradeService.findAllPossibleToUpgrade(anyInt())).thenReturn(allPossibleDto);
        mockMvc.perform(get("/upgrade/all-possible")
            .principal(new UsernamePasswordAuthenticationToken(userDetails, userDetails.getAuthorities()))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
