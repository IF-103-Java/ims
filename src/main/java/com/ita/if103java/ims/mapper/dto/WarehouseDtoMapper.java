package com.ita.if103java.ims.mapper.dto;

import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.entity.Warehouse;
import org.springframework.stereotype.Component;

@Component
public class WarehouseDtoMapper extends AbstractEntityDtoMapper<Warehouse, WarehouseDto> {

    @Override
    public Warehouse toEntity(WarehouseDto dto) {
        if (dto == null) {
            return null;
        } else {
            Warehouse warehouse = new Warehouse();
            warehouse.setId(dto.getId());
            warehouse.setName(dto.getName());
            warehouse.setInfo(dto.getInfo());
            warehouse.setCapacity(dto.getCapacity());
            warehouse.setBottom(dto.isBottom());
            warehouse.setAccountID(dto.getAccountID());
            warehouse.setParentID(dto.getParentID());
            warehouse.setActive(dto.isActive());
            warehouse.setTopWarehouseID(dto.getTopWarehouseID());
            return warehouse;
        }
    }

    @Override
    public WarehouseDto toDto(Warehouse entity) {
        if (entity == null) {
            return null;
        } else {
            WarehouseDto warehouseDto = new WarehouseDto();
            warehouseDto.setId(entity.getId());
            warehouseDto.setName(entity.getName());
            warehouseDto.setInfo(entity.getInfo());
            warehouseDto.setCapacity(entity.getCapacity());
            warehouseDto.setBottom(entity.isBottom());
            warehouseDto.setAccountID(entity.getAccountID());
            warehouseDto.setParentID(entity.getParentID());
            warehouseDto.setActive(entity.isActive());
            warehouseDto.setTopWarehouseID(entity.getTopWarehouseID());
            return warehouseDto;
        }
    }

}
