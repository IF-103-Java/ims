package com.ita.if103java.ims.service;


import com.ita.if103java.ims.dto.AccountDto;

public interface AccountService {
    AccountDto create(AccountDto accountDto);

    AccountDto update(AccountDto accountUpdateDto);

    AccountDto view(Long id);

    boolean delete(Long id);
}
