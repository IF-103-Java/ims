package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.AccountType;

import java.util.List;

public interface AccountTypeDao {

    AccountType findById(Long id);

    List<AccountType> selectAllActive();

    List<AccountType> selectAllPossibleToUpgrade(Integer accountLvl);

    Long minLvlType();
}
