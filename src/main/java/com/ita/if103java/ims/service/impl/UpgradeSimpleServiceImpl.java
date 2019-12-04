package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.entity.Account;
import com.ita.if103java.ims.service.UpgradeService;
import org.springframework.beans.factory.annotation.Autowired;

public class UpgradeSimpleServiceImpl implements UpgradeService {

    AccountDao accountDao;

    @Autowired
    public UpgradeSimpleServiceImpl(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

   @Override
    public void upgradeAccount(Long accountId, Long accountTypeId) {
        if (accountDao.findById(accountId).getTypeId() < accountTypeId) {
            accountDao.upgradeAccount(accountId, accountTypeId);
        }
    }
}
