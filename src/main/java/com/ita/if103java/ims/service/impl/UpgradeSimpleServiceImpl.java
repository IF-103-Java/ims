package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.dao.AccountTypeDao;
import com.ita.if103java.ims.dto.AccountTypeDto;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.exception.service.UpgradationException;
import com.ita.if103java.ims.mapper.dto.AccountTypeDtoMapper;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.EventService;
import com.ita.if103java.ims.service.UpgradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpgradeSimpleServiceImpl implements UpgradeService {

    private AccountDao accountDao;
    private AccountTypeDao accountTypeDao;
    private EventService eventService;
    private AccountTypeDtoMapper accountTypeDtoMapper;

    @Autowired
    public UpgradeSimpleServiceImpl(AccountDao accountDao, AccountTypeDao accountTypeDao, EventService eventService, AccountTypeDtoMapper accountTypeDtoMapper) {
        this.accountDao = accountDao;
        this.accountTypeDao = accountTypeDao;
        this.eventService = eventService;
        this.accountTypeDtoMapper = accountTypeDtoMapper;
    }

    @Override
    public void upgradeAccount(UserDetailsImpl accountAdmin, Long accountTypeId) {
        Integer currentLvl = accountAdmin.getAccountType().getLevel();
        Integer newLvl = accountTypeDao.findById(accountTypeId).getLevel();
        if (currentLvl < newLvl) {
            accountDao.upgradeAccount(accountAdmin.getUser().getAccountId(), accountTypeId);
            Event event = new Event("Account was upgraded to " + accountTypeDao.findById(accountTypeId).getName() + " level.",
                accountAdmin.getUser().getAccountId(), null,
                accountAdmin.getUser().getId(), EventName.ACCOUNT_UPGRADED, null);
            eventService.create(event);
        } else {
            throw new UpgradationException("The level of new type is lower, than current.");
        }
    }

    @Override
    public AccountTypeDto findById(Long id) {
        return accountTypeDtoMapper.toDto(accountTypeDao.findById(id));
    }

    @Override
    public List<AccountTypeDto> findAll() {
        return accountTypeDtoMapper.toDtoList(accountTypeDao.selectAllActive());
    }

    @Override
    public List<AccountTypeDto> findAllPossibleToUpgrade(Integer accountLvl) {
        return accountTypeDtoMapper.toDtoList(accountTypeDao.selectAllPossibleToUpgrade(accountLvl));
    }
}
