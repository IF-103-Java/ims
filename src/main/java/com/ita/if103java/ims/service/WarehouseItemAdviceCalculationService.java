package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.WarehouseIdAdviceDto;
import com.ita.if103java.ims.dto.WarehouseToAssociateDistanceDto;
import com.ita.if103java.ims.dto.WeightAssociateDto;

import java.util.List;

public interface WarehouseItemAdviceCalculationService {
    List<WarehouseIdAdviceDto> calculate(List<WeightAssociateDto> bestSuppliers,
                                         List<WeightAssociateDto> bestClients,
                                         List<WarehouseToAssociateDistanceDto> warehouseAssociateDistances);
}
