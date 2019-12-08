package com.ita.if103java.ims.mapper;

import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.entity.Warehouse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class WarehouseDtoMapper {
    public Warehouse convertWarehouseDtoToWarehouse(WarehouseDto warehouseDto) {
        if (warehouseDto == null) {
            return null;
        } else {
            Warehouse warehouse = new Warehouse();
            warehouse.setId(warehouseDto.getId());
            warehouse.setName(warehouseDto.getName());
            warehouse.setInfo(warehouseDto.getInfo());
            warehouse.setCapacity(warehouseDto.getCapacity());
            warehouse.setBottom(warehouseDto.isBottom());
            warehouse.setAccountID(warehouseDto.getAccountID());
            warehouse.setParentID(warehouseDto.getParentID());
            warehouse.setActive(warehouseDto.isActive());
            warehouse.setTopWarehouseID(warehouseDto.getTopWarehouseID());
            return warehouse;
        }
    }

    public WarehouseDto convertWarehouseToWarehouseDto(Warehouse warehouse) {
        if (warehouse == null) {
            return null;
        } else {
            WarehouseDto warehouseDto = new WarehouseDto();
            warehouseDto.setId(warehouse.getId());
            warehouseDto.setName(warehouse.getName());
            warehouseDto.setInfo(warehouse.getInfo());
            warehouseDto.setCapacity(warehouse.getCapacity());
            warehouseDto.setBottom(warehouse.isBottom());
            warehouseDto.setAccountID(warehouse.getAccountID());
            warehouseDto.setParentID(warehouse.getParentID());
            warehouseDto.setActive(warehouse.isActive());
            warehouseDto.setTopWarehouseID(warehouse.getTopWarehouseID());
            return warehouseDto;
        }
    }

    public List<Warehouse> convertToWarehouseList(List<WarehouseDto> warehouseDtoList) {
        return Optional.ofNullable(warehouseDtoList)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::convertWarehouseDtoToWarehouse)
            .collect(Collectors.toList());
    }

    public List<WarehouseDto> convertToWarehouseDtoList(List<Warehouse> warehouseList) {
        return Optional.ofNullable(warehouseList)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::convertWarehouseToWarehouseDto)
            .collect(Collectors.toList());
    }
}
