package com.ita.if103java.ims.mapper;

import com.ita.if103java.ims.dto.AccountTypeDto;
import com.ita.if103java.ims.entity.AccountType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AccountTypeDtoMapper {
    public AccountType convertAccountTypeDtoToAccountType(AccountTypeDto accountTypeDto) {
        if (accountTypeDto == null) {
            return null;
        } else {
            AccountType accountType = new AccountType();
            accountType.setId(accountTypeDto.getId());
            accountType.setName(accountTypeDto.getName());
            accountType.setPrice(accountTypeDto.getPrice());
            accountType.setLevel(accountTypeDto.getLevel());
            accountType.setMaxWarehouses(accountTypeDto.getMaxWarehouses());
            accountType.setMaxWarehouseDepth(accountTypeDto.getMaxWarehouseDepth());
            accountType.setMaxUsers(accountTypeDto.getMaxUsers());
            accountType.setMaxSuppliers(accountTypeDto.getMaxSuppliers());
            accountType.setMaxClients(accountTypeDto.getMaxClients());
            accountType.setActive(accountTypeDto.isActive());
            return accountType;
        }
    }

    public AccountTypeDto convertAccountTypeToAccountTypeDto(AccountType accountType) {
        if (accountType == null) {
            return null;
        } else {
            AccountTypeDto accountTypeDto = new AccountTypeDto();
            accountTypeDto.setId(accountType.getId());
            accountTypeDto.setName(accountType.getName());
            accountTypeDto.setPrice(accountType.getPrice());
            accountTypeDto.setLevel(accountType.getLevel());
            accountTypeDto.setMaxWarehouses(accountType.getMaxWarehouses());
            accountTypeDto.setMaxWarehouseDepth(accountType.getMaxWarehouseDepth());
            accountTypeDto.setMaxUsers(accountType.getMaxUsers());
            accountTypeDto.setMaxSuppliers(accountType.getMaxSuppliers());
            accountTypeDto.setMaxClients(accountType.getMaxClients());
            accountTypeDto.setActive(accountType.isActive());
            return accountTypeDto;
        }
    }

    public List<AccountType> convertToAccountTypeList(List<AccountTypeDto> accountTypeDtoList) {
        return Optional.ofNullable(accountTypeDtoList)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::convertAccountTypeDtoToAccountType)
            .collect(Collectors.toList());
    }

    public List<AccountTypeDto> convertToAccountTypeDtoList(List<AccountType> accountTypeList) {
        return Optional.ofNullable(accountTypeList)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::convertAccountTypeToAccountTypeDto)
            .collect(Collectors.toList());
    }
}
