package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.dto.AccountDto;
import com.ita.if103java.ims.entity.Account;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
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

    @Autowired
    public AccountServiceImpl(AccountDao accountDao, AccountDtoMapper accountDtoMapper, EventService eventService) {
        this.accountDao = accountDao;
        this.accountDtoMapper = accountDtoMapper;
        this.eventService = eventService;
    }

    @Override
    public AccountDto create(AccountDto accountDto) {
        Account account = accountDao.create(accountDtoMapper.toEntity(accountDto));
        Event event = new Event();
        event.setMessage("New account was created.");
        event.setAccountId(accountDto.getId());
        event.setAuthorId(accountDto.getAdminId());
        event.setName(EventName.ORG_CREATED);
        eventService.create(event);
        return accountDtoMapper.toDto(account);
    }

    @Override
    public AccountDto update(AccountDto accountDto) {
        Account account = accountDao.update(accountDtoMapper.toEntity(accountDto));
        Event event = new Event();
        event.setMessage("Account was updated.");
        event.setAccountId(accountDto.getId());
        event.setAuthorId(accountDto.getAdminId());
        event.setName(EventName.ORG_EDITED);
        eventService.create(event);
        return accountDtoMapper.toDto(account);
    }

    @Override
    public AccountDto view(Long id) {
        Account account = accountDao.findById(id);
        return accountDtoMapper.toDto(account);
    }

    @Override
    public boolean delete(Long id) {
        Event event = new Event();
        event.setMessage("Account was deleted.");
        event.setAccountId(id);
        event.setAuthorId(accountDao.findById(id).getAdminId());
        event.setName(EventName.ORG_DELETED);
        eventService.create(event);
        return accountDao.delete(id);
    }
}
