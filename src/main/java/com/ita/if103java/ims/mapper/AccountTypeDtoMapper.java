package com.ita.if103java.ims.mapper;

import com.ita.if103java.ims.dto.AccountTypeDto;
import com.ita.if103java.ims.entity.AccountType;
import org.springframework.stereotype.Component;

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
}
