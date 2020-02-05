package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.Account;

public interface AccountDao {
    Account create(Account account);

    Account findById(Long id);

    Account update(Account account);

    boolean activate(Long id);

    boolean delete(Long id);

    boolean upgradeAccount(Long id, Long typeId);

    boolean hardDelete(Long accountId);
}
