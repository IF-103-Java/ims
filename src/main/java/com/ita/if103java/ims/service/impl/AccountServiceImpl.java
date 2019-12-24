package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.dao.UserDao;
import com.ita.if103java.ims.dto.AccountDto;
import com.ita.if103java.ims.entity.Account;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.mapper.AccountDtoMapper;
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
    public AccountDto create(User admin, AccountDto accountDto) {
        Account account = accountDao.create(accountDtoMapper.toEntity(accountDto));
        userDao.updateAccountId(admin.getId(), account.getId());
        Event event = new Event("New account was created.", account.getId(), null,
            admin.getId(), EventName.ACCOUNT_CREATED, null);
        eventService.create(event);
        return accountDtoMapper.toDto(account);
    }

    @Override
    public AccountDto update(User admin, AccountDto accountDto) {
        accountDto.setId(admin.getAccountId());
        Account account = accountDao.update(accountDtoMapper.toEntity(accountDto));
        Event event = new Event("Account was updated.", account.getId(), null,
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
            Event event = new Event("Account was deleted.", admin.getAccountId(), null,
                admin.getId(), EventName.ACCOUNT_DELETED, null);
            eventService.create(event);
            return true;
        }
        return false;
    }
}
