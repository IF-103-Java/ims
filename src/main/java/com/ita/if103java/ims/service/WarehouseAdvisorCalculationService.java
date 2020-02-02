package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.warehouse.advice.WarehouseAdviceDto;
import com.ita.if103java.ims.dto.warehouse.advice.WarehouseToAssociateDistancesDto;

import java.util.List;

public interface WarehouseAdvisorCalculationService {
    List<WarehouseAdviceDto> calculate(WarehouseToAssociateDistancesDto warehouseToAssociateDistancesDto);
}
