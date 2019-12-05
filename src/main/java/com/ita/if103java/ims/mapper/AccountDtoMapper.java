package com.ita.if103java.ims.mapper;

import com.ita.if103java.ims.dto.AccountDto;
import com.ita.if103java.ims.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountDtoMapper {
    public Account convertAccountDtoToAccount(AccountDto accountDto) {
        if (accountDto == null) {
            return null;
        } else {
            Account account = new Account();
            account.setId(accountDto.getId());
            account.setName(accountDto.getName());
            account.setTypeId(accountDto.getTypeId());
            account.setAdminId(accountDto.getAdminId());
            account.setActive(accountDto.isActive());
            return account;
        }
    }

    public AccountDto convertAccountToAccountDto(Account account) {
        if (account == null) {
            return null;
        } else {
            AccountDto accountDto = new AccountDto();
            accountDto.setId(account.getId());
            accountDto.setName(account.getName());
            accountDto.setTypeId(account.getTypeId());
            accountDto.setAdminId(account.getAdminId());
            accountDto.setActive(account.isActive());
            return accountDto;
        }

    }
}
