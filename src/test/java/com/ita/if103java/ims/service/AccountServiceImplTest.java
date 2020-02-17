package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.dao.UserDao;
import com.ita.if103java.ims.dto.AccountDto;
import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.Account;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.mapper.dto.AccountDtoMapper;
import com.ita.if103java.ims.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AccountServiceImplTest {

    @Mock
    private AccountDao accountDao;

    @Mock
    private UserDao userDao;

    @Mock
    private EventService eventService;

    @Mock
    private AccountDtoMapper accountDtoMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void create() {
        String accountName = "Home";
        Account account = new Account(1L, accountName, 1L,
            ZonedDateTime.now(ZoneId.systemDefault()), true);
        AccountDto accountDto = new AccountDto(1L, accountName, 1L, true);
        UserDto admin = new UserDto();
        admin.setId(1L);
        Event event = new Event("User " + admin.getFirstName() + " " + admin.getLastName() + " signed up. "
            + "New account \"" + accountName + "\" was created.", account.getId(), null,
            admin.getId(), EventName.ACCOUNT_CREATED, null);

        when(accountDao.create(ArgumentMatchers.any())).thenReturn(account);
        when(userDao.updateAccountId(1L, 1L)).thenReturn(true);
        when(accountDtoMapper.toDto(account)).thenReturn(accountDto);

        assertEquals(accountDto, accountService.create(admin, accountName));

        verify(accountDao, times(1)).create(ArgumentMatchers.any());
        verify(userDao, times(1)).updateAccountId(admin.getId(), account.getId());
        verify(eventService, times(1)).create(event);
        verify(accountDtoMapper, times(1)).toDto(account);
    }

    @Test
    public void update() {
        Account account = new Account(1L, "Home", 1L,
            ZonedDateTime.now(ZoneId.systemDefault()), true);
        String expectedName = "My home";
        AccountDto accountDto = new AccountDto(1L, expectedName, 1L, true);
        User admin = new User();
        admin.setId(1L);
        admin.setAccountId(1L);
        Event event = new Event("Account \"" + expectedName + "\" was updated.", account.getId(), null,
            admin.getId(), EventName.ACCOUNT_EDITED, null);

        when(accountDao.findById(admin.getAccountId())).thenReturn(account);
        when(accountDao.update(account)).thenReturn(account);
        when(accountDtoMapper.toDto(account)).thenReturn(accountDto);

        assertEquals(accountDto, accountService.update(admin, expectedName));

        verify(accountDao, times(1)).findById(admin.getAccountId());
        verify(accountDao, times(1)).update(account);
        verify(eventService, times(1)).create(event);
        verify(accountDtoMapper, times(1)).toDto(account);
    }

    @Test
    public void view() {
        Account account = new Account(1L, "Home", 1L,
            ZonedDateTime.now(ZoneId.systemDefault()), true);
        AccountDto accountDto = new AccountDto(1L, "Home", 1L, true);

        when(accountDao.findById(account.getId())).thenReturn(account);
        when(accountDtoMapper.toDto(account)).thenReturn(accountDto);

        assertEquals(accountDto, accountService.view(1L));

        verify(accountDao, times(1)).findById(account.getId());
        verify(accountDtoMapper, times(1)).toDto(account);
    }

    @Test
    public void deleteSuccess() {
        Account account = new Account(1L, "Home", 1L,
            ZonedDateTime.now(ZoneId.systemDefault()), true);
        User admin = new User();
        admin.setAccountId(1L);
        admin.setId(1L);

        when(accountDao.delete(anyLong())).thenReturn(true);
        when(accountDao.findById(anyLong())).thenReturn(account);

        assertTrue(accountService.delete(admin));

        verify(accountDao, times(1)).delete(admin.getAccountId());
        verify(accountDao, times(1)).findById(admin.getAccountId());
    }

    @Test
    public void deleteFail() {
        Account account = new Account(1L, "Home", 1L,
            ZonedDateTime.now(ZoneId.systemDefault()), true);
        User admin = new User();
        admin.setAccountId(1L);
        admin.setId(1L);

        when(accountDao.delete(anyLong())).thenReturn(false);
        when(accountDao.findById(anyLong())).thenReturn(account);

        assertFalse(accountService.delete(admin));

        verify(accountDao, times(1)).delete(account.getId());
        verify(accountDao, times(0)).findById(anyLong());
    }
}
