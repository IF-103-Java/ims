package com.ita.if103java.ims.mapper;

import com.ita.if103java.ims.dto.AccountCreateDto;
import com.ita.if103java.ims.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountCreateDtoMapper {
    public Account convertAccountCreateDtoToAccount(AccountCreateDto accountCreateDto) {
        if (accountCreateDto == null) {
            return null;
        } else {
            Account account = new Account();
            account.setId(accountCreateDto.getId());
            account.setName(accountCreateDto.getName());
            account.setAdminId(accountCreateDto.getAdminId());
            return account;
        }
    }

    public AccountCreateDto convertAccountToAccountCreateDto(Account account) {
        if (account == null) {
            return null;
        } else {
            AccountCreateDto accountCreateDto = new AccountCreateDto();
            accountCreateDto.setId(account.getId());
            accountCreateDto.setName(account.getName());
            accountCreateDto.setAdminId(account.getAdminId());
            return accountCreateDto;
        }

    }
}
