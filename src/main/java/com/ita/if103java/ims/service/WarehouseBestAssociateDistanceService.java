package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.warehouse.advice.TopWarehouseAddressDto;
import com.ita.if103java.ims.dto.warehouse.advice.associate.BestWeightedAssociateDto;
import com.ita.if103java.ims.dto.warehouse.advice.distance.WarehouseToAssociateDistancesDto;

import java.util.List;

public interface WarehouseBestAssociateDistanceService {
    WarehouseToAssociateDistancesDto getDistances(List<TopWarehouseAddressDto> warehouses,
                                                  List<BestWeightedAssociateDto> associates);
}
