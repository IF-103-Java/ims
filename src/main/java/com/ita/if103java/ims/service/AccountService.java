package com.ita.if103java.ims.service;


import com.ita.if103java.ims.dto.AccountDto;
import com.ita.if103java.ims.dto.UserDto;

public interface AccountService {
    AccountDto create(UserDto admin, String accountName);

    AccountDto update(UserDto admin, AccountDto accountUpdateDto);

    AccountDto view(Long id);

    boolean delete(UserDto admin);
}
