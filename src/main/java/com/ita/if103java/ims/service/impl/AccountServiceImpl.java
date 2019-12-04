package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.dto.AccountCreateDto;
import com.ita.if103java.ims.dto.AccountUpdateDto;
import com.ita.if103java.ims.entity.Account;
import com.ita.if103java.ims.mapper.AccountCreateDtoMapper;
import com.ita.if103java.ims.mapper.AccountUpdateDtoMapper;
import com.ita.if103java.ims.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountServiceImpl implements AccountService {

    private AccountDao accountDao;
    private AccountCreateDtoMapper accountCreateDtoMapper;
    private AccountUpdateDtoMapper accountUpdateDtoMapper;

    @Autowired
    public AccountServiceImpl(AccountDao accountDao, AccountCreateDtoMapper accountCreateDtoMapper, AccountUpdateDtoMapper accountUpdateDtoMapper) {
        this.accountDao = accountDao;
        this.accountCreateDtoMapper = accountCreateDtoMapper;
        this.accountUpdateDtoMapper = accountUpdateDtoMapper;
    }

    @Override
    public AccountCreateDto create(AccountCreateDto accountCreateDto) {
        Account account = accountDao.create(accountCreateDtoMapper.convertAccountCreateDtoToAccount(accountCreateDto));
        return accountCreateDtoMapper.convertAccountToAccountCreateDto(account);
    }

    @Override
    public AccountUpdateDto update(AccountUpdateDto accountUpdateDto) {
        Account account = accountDao.update(accountUpdateDtoMapper.convertAccountUpdateDtoToAccount(accountUpdateDto));
        return accountUpdateDtoMapper.convertAccountToAccountUpdateDto(account);
    }

    @Override
    public boolean delete(Long id) {
        return accountDao.delete(id);
    }
}
