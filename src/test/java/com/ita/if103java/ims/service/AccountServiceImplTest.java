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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
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

    private Account account;
    private AccountDto accountDto;
    private User admin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        account = new Account(1L, "Home", 1L,
            ZonedDateTime.now(ZoneId.systemDefault()), true);
        accountDto = new AccountDto(1L, "Home", 1L, true);
        admin = new User();
        admin.setAccountId(1L);
        admin.setId(1L);
    }

    @Test
    public void create() {
        String accountName = "Home";
        UserDto admin = new UserDto();
        admin.setId(1L);
        Event event = new Event("User " + admin.getFirstName() + " " + admin.getLastName() + " signed up. "
            + "New account \"" + accountName + "\" was created.", account.getId(), null,
            admin.getId(), EventName.ACCOUNT_CREATED, null);

        when(accountDao.create(ArgumentMatchers.any())).thenReturn(account);
        when(userDao.updateAccountId(admin.getId(), account.getId())).thenReturn(true);
        when(accountDtoMapper.toDto(account)).thenReturn(accountDto);

        assertEquals(accountDto, accountService.create(admin, accountName));

        verify(accountDao).create(ArgumentMatchers.any());
        verify(userDao).updateAccountId(admin.getId(), account.getId());
        verify(eventService).create(event);
        verify(accountDtoMapper).toDto(account);
    }

    @Test
    public void update() {
        String expectedName = "My home";
        AccountDto updatedAccountDto = new AccountDto(1L, expectedName, 1L, true);
        Event event = new Event("Account \"" + expectedName + "\" was updated.", account.getId(), null,
            admin.getId(), EventName.ACCOUNT_EDITED, null);

        when(accountDao.findById(admin.getAccountId())).thenReturn(account);
        when(accountDao.update(account)).thenReturn(account);
        when(accountDtoMapper.toDto(account)).thenReturn(updatedAccountDto);

        assertEquals(updatedAccountDto, accountService.update(admin, expectedName));

        verify(accountDao).findById(admin.getAccountId());
        verify(accountDao).update(account);
        verify(eventService).create(event);
        verify(accountDtoMapper).toDto(account);
    }

    @Test
    public void view() {
        when(accountDao.findById(account.getId())).thenReturn(account);
        when(accountDtoMapper.toDto(account)).thenReturn(accountDto);

        assertEquals(accountDto, accountService.view(1L));

        verify(accountDao).findById(account.getId());
        verify(accountDtoMapper).toDto(account);
    }

    @Test
    public void deleteSuccess() {
        Event event = new Event("Account \"" + account.getName() + "\" was deleted.", admin.getAccountId(), null,
            admin.getId(), EventName.ACCOUNT_DELETED, null);

        when(accountDao.delete(anyLong())).thenReturn(true);
        when(accountDao.findById(anyLong())).thenReturn(account);

        assertTrue(accountService.delete(admin));

        verify(accountDao).delete(admin.getAccountId());
        verify(accountDao).findById(admin.getAccountId());
        verify(eventService).create(event);
    }

    @Test
    public void deleteFail() {
        when(accountDao.delete(anyLong())).thenReturn(false);
        when(accountDao.findById(anyLong())).thenReturn(account);

        assertFalse(accountService.delete(admin));

        verify(accountDao).delete(account.getId());
        verify(accountDao, never()).findById(anyLong());
        verify(eventService, never()).create(any());
    }
}
