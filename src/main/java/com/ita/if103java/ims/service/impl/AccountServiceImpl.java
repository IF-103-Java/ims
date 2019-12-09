package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.dto.AccountDto;
import com.ita.if103java.ims.entity.Account;
import com.ita.if103java.ims.mapper.AccountDtoMapper;
import com.ita.if103java.ims.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountServiceImpl implements AccountService {

    private AccountDao accountDao;
    private AccountDtoMapper accountDtoMapper;

    @Autowired
    public AccountServiceImpl(AccountDao accountDao, AccountDtoMapper accountDtoMapper) {
        this.accountDao = accountDao;
        this.accountDtoMapper = accountDtoMapper;
    }

    @Override
    public AccountDto create(AccountDto accountDto) {
        Account account = accountDao.create(accountDtoMapper.toEntity(accountDto));
        return accountDtoMapper.toDto(account);
    }

    @Override
    public AccountDto update(AccountDto accountDto) {
        Account account = accountDao.update(accountDtoMapper.toEntity(accountDto));
        return accountDtoMapper.toDto(account);
    }

    @Override
    public AccountDto view(Long id) {
        Account account = accountDao.findById(id);
        return accountDtoMapper.toDto(account);
    }

    @Override
    public boolean delete(Long id) {
        return accountDao.delete(id);
    }
}
