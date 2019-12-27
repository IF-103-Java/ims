package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.AssociateAddressDto;
import com.ita.if103java.ims.dto.WarehouseAddressDto;
import com.ita.if103java.ims.dto.WarehouseToAssociateDistanceDto;

import java.util.List;

public interface WarehouseAssociateDistanceService {
    List<WarehouseToAssociateDistanceDto> getDistances(List<WarehouseAddressDto> warehouseAddresses,
                                                       List<AssociateAddressDto> supplierAddresses,
                                                       List<AssociateAddressDto> clientAddresses);
}
