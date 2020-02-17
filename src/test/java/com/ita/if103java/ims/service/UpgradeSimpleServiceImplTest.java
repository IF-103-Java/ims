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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void upgradeAccountSuccess() {
        Long newAccountTypeId = 2L;
        User user = new User();
        user.setAccountId(1L);
        AccountType currentAccountType = new AccountType();
        currentAccountType.setLevel(1);
        AccountType newAccountType = new AccountType();
        newAccountType.setLevel(2);
        newAccountType.setName("Premium");
        UserDetailsImpl accountAdmin = new UserDetailsImpl(user, currentAccountType);
        Event event = new Event("Account was upgraded to " + newAccountType.getName() + " level.",
            accountAdmin.getUser().getAccountId(), null,
            accountAdmin.getUser().getId(), EventName.ACCOUNT_UPGRADED, null);

        when(accountTypeDao.findById(newAccountTypeId)).thenReturn(newAccountType);
        when(accountDao.upgradeAccount(currentAccountType.getId(), newAccountTypeId)).thenReturn(true);

        upgradeService.upgradeAccount(accountAdmin, newAccountTypeId);

        verify(accountTypeDao, times(2)).findById(newAccountTypeId);
        verify(accountDao, times(1)).upgradeAccount(accountAdmin.getUser().getAccountId(), newAccountTypeId);
        verify(eventService, times(1)).create(event);
    }

    @Test
    public void upgradeAccountFail() {
        Long newAccountTypeId = 2L;
        User user = new User();
        user.setAccountId(1L);
        AccountType currentAccountType = new AccountType();
        currentAccountType.setLevel(2);
        AccountType newAccountType = new AccountType();
        newAccountType.setLevel(1);
        UserDetailsImpl accountAdmin = new UserDetailsImpl(user, currentAccountType);

        when(accountTypeDao.findById(newAccountTypeId)).thenReturn(newAccountType);
        when(accountDao.upgradeAccount(currentAccountType.getId(), newAccountTypeId)).thenReturn(true);

        assertThrows(UpgradationException.class, () -> upgradeService.upgradeAccount(accountAdmin, newAccountTypeId));

        verify(accountTypeDao, times(1)).findById(newAccountTypeId);
        verify(accountDao, times(0)).upgradeAccount(anyLong(), anyLong());
        verify(eventService, times(0)).create(any());
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

        verify(accountTypeDao, times(1)).findById(accountId);
        verify(accountTypeDtoMapper, times(1)).toDto(accountType);
    }

    @Test
    public void findAll() {
        List<AccountType> allActiveAccountTypes = Arrays.asList(
            new AccountType(1L, "Basic", 0.0, 1, 3, 3,
                3, 3, 3, false,
                false, true),
            new AccountType(2L, "Premium", 500.0, 2, 100, 100,
                100, 100, 100, true,
                true, true)
        );
        List<AccountTypeDto> allActiveAccountTypesDto = Arrays.asList(
            new AccountTypeDto(1L, "Basic", 0.0, 1, 3, 3,
                3, 3, 3, false,
                false, true),
            new AccountTypeDto(2L, "Premium", 500.0, 2, 100, 100,
                100, 100, 100, true,
                true, true)
        );

        when(accountTypeDao.selectAllActive()).thenReturn(allActiveAccountTypes);
        when(accountTypeDtoMapper.toDtoList(allActiveAccountTypes)).thenReturn(allActiveAccountTypesDto);

        assertEquals(allActiveAccountTypesDto, upgradeService.findAll());

        verify(accountTypeDao, times(1)).selectAllActive();
        verify(accountTypeDtoMapper, times(1)).toDtoList(allActiveAccountTypes);
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

        verify(accountTypeDao, times(1)).selectAllPossibleToUpgrade(anyInt());
        verify(accountTypeDtoMapper, times(1)).toDtoList(allPossible);
    }
}
