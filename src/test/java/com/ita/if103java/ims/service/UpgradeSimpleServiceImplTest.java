package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.dao.AccountTypeDao;
import com.ita.if103java.ims.dto.AccountTypeDto;
import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.exception.service.UpgradationException;
import com.ita.if103java.ims.mapper.dto.AccountTypeDtoMapper;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.impl.UpgradeSimpleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpgradeSimpleServiceImplTest {

    @Mock
    AccountDao accountDao;

    @Mock
    AccountTypeDao accountTypeDao;

    @Mock
    EventService eventService;

    @Mock
    AccountTypeDtoMapper accountTypeDtoMapper;

    @InjectMocks
    private UpgradeSimpleServiceImpl upgradeService;

    private Long newAccountTypeId;
    private AccountType currentAccountType;
    private AccountType newAccountType;
    private UserDetailsImpl accountAdmin;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        User user = new User();
        user.setAccountId(1L);
        newAccountTypeId = 2L;
        currentAccountType = new AccountType();
        newAccountType = new AccountType();
        accountAdmin = new UserDetailsImpl(user, currentAccountType);
    }

    @Test
    public void upgradeAccountSuccess() {
        currentAccountType.setLevel(1);
        newAccountType.setLevel(2);
        newAccountType.setName("Premium");
        Event event = new Event("Account was upgraded to " + newAccountType.getName() + " level.",
            accountAdmin.getUser().getAccountId(), null,
            accountAdmin.getUser().getId(), EventName.ACCOUNT_UPGRADED, null);

        when(accountTypeDao.findById(newAccountTypeId)).thenReturn(newAccountType);
        when(accountDao.upgradeAccount(currentAccountType.getId(), newAccountTypeId)).thenReturn(true);

        upgradeService.upgradeAccount(accountAdmin, newAccountTypeId);

        verify(accountTypeDao, times(2)).findById(newAccountTypeId);
        verify(accountDao).upgradeAccount(accountAdmin.getUser().getAccountId(), newAccountTypeId);
        verify(eventService).create(event);
    }

    @Test
    public void upgradeAccountFail() {
        currentAccountType.setLevel(2);
        newAccountType.setLevel(1);

        when(accountTypeDao.findById(newAccountTypeId)).thenReturn(newAccountType);
        when(accountDao.upgradeAccount(currentAccountType.getId(), newAccountTypeId)).thenReturn(true);

        assertThrows(UpgradationException.class, () -> upgradeService.upgradeAccount(accountAdmin, newAccountTypeId));

        verify(accountTypeDao).findById(newAccountTypeId);
        verify(accountDao, never()).upgradeAccount(anyLong(), anyLong());
        verify(eventService, never()).create(any());
    }


    @Test
    public void findById() {
        Long accountId = 1L;
        AccountType accountType = new AccountType(2L, "Premium", 500.0, 2, 100, 100,
            100, 100, 100, true,
            true, true);
        AccountTypeDto accountTypeDto = new AccountTypeDto(2L, "Premium", 500.0, 2, 100, 100,
            100, 100, 100, true,
            true, true);
        when(accountTypeDao.findById(anyLong())).thenReturn(accountType);
        when(accountTypeDtoMapper.toDto(accountType)).thenReturn(accountTypeDto);

        upgradeService.findById(accountId);

        verify(accountTypeDao).findById(accountId);
        verify(accountTypeDtoMapper).toDto(accountType);
    }

    @Test
    public void findAllPossibleToUpgrade() {
        AccountType premium = new AccountType(2L, "Premium", 500.0, 3, 100, 100,
            100, 100, 100, true,
            true, true);
        List<AccountType> allActiveAccountTypes = Arrays.asList(
            new AccountType(1L, "Basic", 0.0, 1, 3, 3,
                3, 3, 3, false,
                false, true),
            new AccountType(2L, "Medium", 500.0, 2, 100, 100,
                100, 100, 100, true,
                true, true),
            premium,
            new AccountType(2L, "Super premium", 500.0, 4, 100, 100,
                100, 100, 100, true,
                true, false)
        );
        List<AccountType> allPossible = Arrays.asList(premium);
        List<AccountTypeDto> allPossibleDto = Arrays.asList(
            new AccountTypeDto(2L, "Premium", 500.0, 2, 100, 100,
                100, 100, 100, true,
                true, true)
        );


        when(accountTypeDao.selectAllPossibleToUpgrade(anyInt())).thenReturn(allPossible);
        when(accountTypeDtoMapper.toDtoList(allPossible)).thenReturn(allPossibleDto);

        assertEquals(allPossibleDto, upgradeService.findAllPossibleToUpgrade(2));

        verify(accountTypeDao).selectAllPossibleToUpgrade(anyInt());
        verify(accountTypeDtoMapper).toDtoList(allPossible);
    }
}
