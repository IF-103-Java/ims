package com.ita.if103java.ims.mapper;

import com.ita.if103java.ims.dto.AccountTypeDto;
import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.entity.Item;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AccountTypeDtoMapper extends AbstractEntityDtoMapper<AccountType, AccountTypeDto> {
    public AccountType toEntity(AccountTypeDto dto) {
        if (dto == null) {
            return null;
        } else {
            AccountType accountType = new AccountType();
            accountType.setId(dto.getId());
            accountType.setName(dto.getName());
            accountType.setPrice(dto.getPrice());
            accountType.setLevel(dto.getLevel());
            accountType.setMaxWarehouses(dto.getMaxWarehouses());
            accountType.setMaxWarehouseDepth(dto.getMaxWarehouseDepth());
            accountType.setMaxUsers(dto.getMaxUsers());
            accountType.setMaxSuppliers(dto.getMaxSuppliers());
            accountType.setMaxClients(dto.getMaxClients());
            accountType.setActive(dto.isActive());
            return accountType;
        }
    }

    @Override
    public AccountTypeDto toDto(AccountType entity) {
        if (entity == null) {
            return null;
        } else {
            AccountTypeDto accountTypeDto = new AccountTypeDto();
            accountTypeDto.setId(entity.getId());
            accountTypeDto.setName(entity.getName());
            accountTypeDto.setPrice(entity.getPrice());
            accountTypeDto.setLevel(entity.getLevel());
            accountTypeDto.setMaxWarehouses(entity.getMaxWarehouses());
            accountTypeDto.setMaxWarehouseDepth(entity.getMaxWarehouseDepth());
            accountTypeDto.setMaxUsers(entity.getMaxUsers());
            accountTypeDto.setMaxSuppliers(entity.getMaxSuppliers());
            accountTypeDto.setMaxClients(entity.getMaxClients());
            accountTypeDto.setActive(entity.isActive());
            return accountTypeDto;
        }
    }

    public List<AccountType> convertToAccountTypeList(List<AccountTypeDto> accountTypeDtoList) {
        return Optional.ofNullable(accountTypeDtoList)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::toEntity)
            .collect(Collectors.toList());
    }

    public List<AccountTypeDto> convertToAccountTypeDtoList(List<AccountType> accountTypeList) {
        return Optional.ofNullable(accountTypeList)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }
}
