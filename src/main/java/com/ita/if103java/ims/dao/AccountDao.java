package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.Account;

public interface AccountDao {
    Account create(Account account);

    Account findById(Long id);

    Account findByAdminId(Long id);

    Account update(Account account);

    boolean delete(Long id);

    boolean updateToPremium(Long id);
}
