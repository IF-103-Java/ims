package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.dao.AccountTypeDao;
import com.ita.if103java.ims.service.UpgradeService;
import org.springframework.beans.factory.annotation.Autowired;

public class UpgradeSimpleServiceImpl implements UpgradeService {

    AccountDao accountDao;
    AccountTypeDao accountTypeDao;

    @Autowired
    public UpgradeSimpleServiceImpl(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public void upgradeAccount(Long accountId, Long accountTypeId) {
        Integer currentLvl = accountTypeDao.findById(accountDao.findById(accountId).getTypeId()).getLevel();
        Integer newLvl = accountTypeDao.findById(accountTypeId).getLevel();
        if (currentLvl < newLvl) {
            accountDao.upgradeAccount(accountId, accountTypeId);
        }
    }
}
