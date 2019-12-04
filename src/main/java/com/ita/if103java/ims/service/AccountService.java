package com.ita.if103java.ims.service;


import com.ita.if103java.ims.dto.AccountCreateDto;
import com.ita.if103java.ims.dto.AccountUpdateDto;

public interface AccountService {
    AccountCreateDto create(AccountCreateDto accountCreateDto);

    AccountUpdateDto update(AccountUpdateDto accountUpdateDto);

    boolean delete(Long id);
}
