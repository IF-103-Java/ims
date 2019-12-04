package com.ita.if103java.ims.mapper;

import com.ita.if103java.ims.dto.AccountUpdateDto;
import com.ita.if103java.ims.entity.Account;

public class AccountUpdateDtoMapper {
    public Account convertAccountUpdateDtoToAccount(AccountUpdateDto accountUpdateDto) {
        if (accountUpdateDto == null) {
            return null;
        } else {
            Account account = new Account();
            account.setId(accountUpdateDto.getId());
            account.setName(accountUpdateDto.getName());
            return account;
        }
    }

    public AccountUpdateDto convertAccountToAccountUpdateDto(Account account) {
        if (account == null) {
            return null;
        } else {
            AccountUpdateDto accountUpdateDto = new AccountUpdateDto();
            accountUpdateDto.setId(account.getId());
            accountUpdateDto.setName(account.getName());
            return accountUpdateDto;
        }

    }
}
