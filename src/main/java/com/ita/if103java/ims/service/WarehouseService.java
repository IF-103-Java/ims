package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.WarehouseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface WarehouseService {
    WarehouseDto add(WarehouseDto warehouseDto);

    WarehouseDto findWarehouseById(Long id);

    List<WarehouseDto> findWarehousesByParentId(Long accountID);

    WarehouseDto update(WarehouseDto warehouseDto);

    boolean softDelete(Long id);

    List<WarehouseDto> findAll();

}
