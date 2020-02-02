package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.warehouse.advice.BestAssociatesDto.WeightedBestAssociateDto;
import com.ita.if103java.ims.dto.warehouse.advice.TopWarehouseAddressDto;
import com.ita.if103java.ims.dto.warehouse.advice.WarehouseToAssociateDistancesDto;

import java.util.List;

public interface WarehouseBestAssociateDistanceService {
    WarehouseToAssociateDistancesDto getDistances(List<TopWarehouseAddressDto> warehouses,
                                                  List<WeightedBestAssociateDto> associates);
}
