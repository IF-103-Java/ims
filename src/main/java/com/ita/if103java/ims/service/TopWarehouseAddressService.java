package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.warehouse.advice.TopWarehouseAddressDto;

import java.util.List;

public interface TopWarehouseAddressService {
    List<TopWarehouseAddressDto> findAll(Long accountId);
}
