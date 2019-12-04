package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.AccountTypeDto;

import java.util.List;

public interface AccountTypeService {
    List<AccountTypeDto> findAll();
}
