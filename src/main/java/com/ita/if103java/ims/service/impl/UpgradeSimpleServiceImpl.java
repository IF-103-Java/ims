package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.dao.AccountTypeDao;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.service.EventService;
import com.ita.if103java.ims.service.UpgradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpgradeSimpleServiceImpl implements UpgradeService {

    private AccountDao accountDao;
    private AccountTypeDao accountTypeDao;
    private EventService eventService;

    @Autowired
    public UpgradeSimpleServiceImpl(AccountDao accountDao, AccountTypeDao accountTypeDao, EventService eventService) {
        this.accountDao = accountDao;
        this.accountTypeDao = accountTypeDao;
        this.eventService = eventService;
    }


    @Override
    public void upgradeAccount(Long accountId, Long accountTypeId) {
        Integer currentLvl = accountTypeDao.findById(accountDao.findById(accountId).getTypeId()).getLevel();
        Integer newLvl = accountTypeDao.findById(accountTypeId).getLevel();
        if (currentLvl < newLvl) {
            accountDao.upgradeAccount(accountId, accountTypeId);

            Event event = new Event();
            event.setMessage("Account was upgraded to " + accountTypeDao.findById(accountTypeId).getName() + " level.");
            event.setAccountId(accountId);
            event.setAuthorId(accountDao.findById(accountId).getAdminId());
            event.setName(EventName.ORG_UPGRADED);
            eventService.create(event);
        }
    }
}
