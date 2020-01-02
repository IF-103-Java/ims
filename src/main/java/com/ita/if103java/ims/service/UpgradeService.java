package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.AccountTypeDto;
import com.ita.if103java.ims.entity.User;

import java.util.List;

public interface UpgradeService {

    void upgradeAccount(User accountAdmin, Long accountTypeId);

    AccountTypeDto findById(Long id);

    List<AccountTypeDto> findAll();

    List<AccountTypeDto> findAllPossibleToUpgrade(Long typeId);
}
