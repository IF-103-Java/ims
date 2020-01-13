package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.security.UserDetailsImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface WarehouseService {
    WarehouseDto add(WarehouseDto warehouseDto);

    WarehouseDto findWarehouseById(Long id);

    List<WarehouseDto> findWarehousesByTopLevelId(Long accountID);

    WarehouseDto update(WarehouseDto warehouseDto);

    boolean softDelete(Long id);

    List<WarehouseDto> findAll();

    Map<Long, String> findWarehouseNames(UserDetailsImpl user);

}
