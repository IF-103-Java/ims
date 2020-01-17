package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.dao.UserDao;
import com.ita.if103java.ims.dto.AccountDto;
import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.Account;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.mapper.dto.AccountDtoMapper;
import com.ita.if103java.ims.service.AccountService;
import com.ita.if103java.ims.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountDao accountDao;
    private AccountDtoMapper accountDtoMapper;
    private EventService eventService;
    private UserDao userDao;

    @Autowired
    public AccountServiceImpl(AccountDao accountDao, AccountDtoMapper accountDtoMapper, EventService eventService, UserDao userDao) {
        this.accountDao = accountDao;
        this.accountDtoMapper = accountDtoMapper;
        this.eventService = eventService;
        this.userDao = userDao;
    }


    @Override
    public AccountDto create(UserDto admin, String accountName) {
        Account account = accountDao.create(new Account(accountName));
        userDao.updateAccountId(admin.getId(), account.getId());
        Event event = new Event("User " + admin.getFirstName() + " " + admin.getLastName() + " signed up. "
            + "New account \"" + accountName + "\" was created.", account.getId(), null,
            admin.getId(), EventName.ACCOUNT_CREATED, null);
        eventService.create(event);
        return accountDtoMapper.toDto(account);
    }

    @Override
    public AccountDto update(User admin, String name) {
        Account account = accountDao.findById(admin.getAccountId());
        account.setName(name);
        account = accountDao.update(account);
        Event event = new Event("Account \"" + name + "\" was updated.", account.getId(), null,
            admin.getId(), EventName.ACCOUNT_EDITED, null);
        eventService.create(event);
        return accountDtoMapper.toDto(account);
    }

    @Override
    public AccountDto view(Long id) {
        Account account = accountDao.findById(id);
        return accountDtoMapper.toDto(account);
    }

    @Override
    public boolean delete(User admin) {
        if (accountDao.delete(admin.getAccountId())) {
            Event event = new Event("Account \"" + accountDao.findById(admin.getAccountId()).getName() + "\" was deleted.", admin.getAccountId(), null,
                admin.getId(), EventName.ACCOUNT_DELETED, null);
            eventService.create(event);
            return true;
        }
        return false;
    }
}
