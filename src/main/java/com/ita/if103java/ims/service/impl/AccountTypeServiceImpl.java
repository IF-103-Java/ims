package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.AccountTypeDao;
import com.ita.if103java.ims.dto.AccountTypeDto;
import com.ita.if103java.ims.mapper.AccountTypeDtoMapper;
import com.ita.if103java.ims.service.AccountTypeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AccountTypeServiceImpl implements AccountTypeService {

    AccountTypeDao accountTypeDao;
    AccountTypeDtoMapper accountTypeDtoMapper;

    @Autowired
    public AccountTypeServiceImpl(AccountTypeDao accountTypeDao, AccountTypeDtoMapper accountTypeDtoMapper) {
        this.accountTypeDao = accountTypeDao;
        this.accountTypeDtoMapper = accountTypeDtoMapper;
    }

    @Override
    public List<AccountTypeDto> findAll() {
        return accountTypeDtoMapper.convertToAccountTypeDtoList(accountTypeDao.selectAllActive());
    }

    @Override
    public List<AccountTypeDto> findAllPossibleToUpgrade(Long typeId) {
        return accountTypeDtoMapper.convertToAccountTypeDtoList(accountTypeDao.selectAllPossibleToUpgrade(typeId));
    }
}
