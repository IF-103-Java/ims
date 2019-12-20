package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.AddressDto;
import com.ita.if103java.ims.dto.WarehouseToAssociateDistanceDto;

import java.util.List;

public interface DistanceMatrixService {
    List<WarehouseToAssociateDistanceDto> getDistances(List<AddressDto> warehouseAddresses,
                                                       List<AddressDto> supplierAddresses,
                                                       List<AddressDto> clientAddresses);
}
