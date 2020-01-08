package com.ita.if103java.ims.mapper.dto;

import com.ita.if103java.ims.dto.AccountDto;
import com.ita.if103java.ims.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountDtoMapper extends AbstractEntityDtoMapper<Account, AccountDto> {

    @Override
    public Account toEntity(AccountDto dto) {
        if (dto == null) {
            return null;
        } else {
            Account account = new Account();
            account.setId(dto.getId());
            account.setName(dto.getName());
            account.setTypeId(dto.getTypeId());
            account.setActive(dto.isActive());
            return account;
        }
    }

    @Override
    public AccountDto toDto(Account entity) {
        if (entity == null) {
            return null;
        } else {
            AccountDto accountDto = new AccountDto();
            accountDto.setId(entity.getId());
            accountDto.setName(entity.getName());
            accountDto.setTypeId(entity.getTypeId());
            accountDto.setActive(entity.isActive());
            return accountDto;
        }
    }
}
