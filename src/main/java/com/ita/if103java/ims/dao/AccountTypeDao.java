package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.AccountType;

public interface AccountTypeDao {
    AccountType create(AccountType accountType);

    AccountType findById(Long id);

    AccountType findByName(String name);

    AccountType update(AccountType account);

    boolean delete(Long id);
}
