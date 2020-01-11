package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.security.UserDetailsImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface WarehouseService {
    WarehouseDto add(WarehouseDto warehouseDto, UserDetailsImpl user);

    WarehouseDto findById(Long id);

    List<WarehouseDto> findWarehousesByTopLevelId(Long accountId, UserDetailsImpl user);

    WarehouseDto update(WarehouseDto warehouseDto, UserDetailsImpl user);

    boolean softDelete(Long id, UserDetailsImpl user);

    List<WarehouseDto> findAll(Pageable pageable, UserDetailsImpl user);

    Map<Long, String> findWarehouseNames(UserDetailsImpl user);

}
