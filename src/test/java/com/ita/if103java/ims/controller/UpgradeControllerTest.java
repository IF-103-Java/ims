package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.AccountDto;
import com.ita.if103java.ims.dto.AccountTypeDto;
import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.exception.service.UpgradationException;
import com.ita.if103java.ims.handler.GlobalExceptionHandler;
import com.ita.if103java.ims.security.SecurityInterceptor;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.UpgradeService;
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
import java.util.Arrays;
import java.util.List;

import static com.ita.if103java.ims.security.SecurityInterceptor.init;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
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
    private ZonedDateTime currentDateTime;
    private AccountTypeDto accountTypeDto;
    private UpgradationException upgradationException;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(upgradeController)
            .setControllerAdvice(GlobalExceptionHandler.class)
            .addInterceptors(new SecurityInterceptor())
            .setCustomArgumentResolvers(
                new AuthenticationPrincipalArgumentResolver())
            .build();

        accountTypeId = 2L;

        currentDateTime = ZonedDateTime.now(ZoneId.systemDefault());
        user = new User(1L, "First name", "Last name", "im.user@gmail.com", "nfdfsasf", Role.ROLE_ADMIN,
            currentDateTime, currentDateTime, true, "rddfgfd", 3L);
        accountType = new AccountType(2L, "Premium", 300.0, 2,
            100, 100, 100, 100, 100,
            true, true, true);
        userDetails = new UserDetailsImpl(user, accountType);
        accountDto = new AccountDto(3L, "Name", 2L, true);
        accountTypeDto = new AccountTypeDto(2L, "Premium", 300.0, 2,
            100, 100, 100, 100, 100,
            true, true, true);

        upgradationException = new UpgradationException("Upgrade exception");
    }

    @Test
    void upgrade_SuccessFlow() throws Exception {
        mockMvc.perform(put("/upgrade/" + accountTypeId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(upgradeService).upgradeAccount(any(UserDetailsImpl.class), eq(accountTypeId));
    }

    @Test
    void upgrade_UpgradationException() throws Exception {
        doThrow(upgradationException).when(upgradeService).upgradeAccount(userDetails, accountTypeId);

        mockMvc.perform(put("/upgrade/" + accountTypeId)
            .principal(init(userDetails))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(upgradeService).upgradeAccount(userDetails, accountTypeId);
    }

    @Test
    void findCurrentType_SuccessFlow() throws Exception {

        when(upgradeService.findById(userDetails.getAccountType().getId())).thenReturn(accountTypeDto);
        mockMvc.perform(get("/upgrade/")
            .principal(init(userDetails))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(accountTypeDto.getId()))
            .andExpect(jsonPath("$.name").value(accountTypeDto.getName()))
            .andExpect(jsonPath("$.price").value(accountTypeDto.getPrice()))
            .andExpect(jsonPath("$.level").value(accountTypeDto.getLevel()))
            .andExpect(jsonPath("$.maxWarehouses").value(accountTypeDto.getMaxWarehouses()))
            .andExpect(jsonPath("$.maxWarehouseDepth").value(accountTypeDto.getMaxWarehouseDepth()))
            .andExpect(jsonPath("$.maxUsers").value(accountTypeDto.getMaxUsers()))
            .andExpect(jsonPath("$.maxSuppliers").value(accountTypeDto.getMaxSuppliers()))
            .andExpect(jsonPath("$.maxClients").value(accountTypeDto.getMaxClients()))
            .andExpect(jsonPath("$.deepWarehouseAnalytics").value(accountTypeDto.isDeepWarehouseAnalytics()))
            .andExpect(jsonPath("$.itemStorageAdvisor").value(accountTypeDto.isItemStorageAdvisor()))
            .andExpect(jsonPath("$.active").value(accountTypeDto.isActive()));

        verify(upgradeService).findById(userDetails.getAccountType().getId());
    }

    @Test
    void findAllPossible_SuccessFlow() throws Exception {
        List<AccountTypeDto> allPossibleDto = Arrays.asList(
            new AccountTypeDto(1L, "Basic", 0.0, 1, 3, 3,
                3, 3, 3, false,
                false, true),
            new AccountTypeDto(2L, "Premium", 500.0, 2, 100, 100,
                100, 100, 100, true,
                true, true)
        );
        when(upgradeService.findAllPossibleToUpgrade(userDetails.getAccountType().getLevel())).thenReturn(allPossibleDto);
        mockMvc.perform(get("/upgrade/all-possible")
            .principal(init(userDetails))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].id").value(allPossibleDto.get(0).getId()))
            .andExpect(jsonPath("$.[1].name").value(allPossibleDto.get(1).getName()));

        verify(upgradeService).findAllPossibleToUpgrade(userDetails.getAccountType().getLevel());
    }
}
