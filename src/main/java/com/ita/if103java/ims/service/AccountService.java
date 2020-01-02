package com.ita.if103java.ims.service;


import com.ita.if103java.ims.dto.AccountDto;
import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.User;

public interface AccountService {
    AccountDto create(UserDto adminDto, String accountName);

    AccountDto update(User admin, AccountDto accountUpdateDto);

    AccountDto view(Long id);

    boolean delete(User admin);
}
