package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.AccountType;

import java.util.List;

public interface AccountTypeDao {
    AccountType create(AccountType accountType);

    AccountType findById(Long id);

    AccountType findByName(String name);

    List<AccountType> selectAll();

    AccountType update(AccountType account);

    Long minLvlType();

    boolean delete(Long id);
}
