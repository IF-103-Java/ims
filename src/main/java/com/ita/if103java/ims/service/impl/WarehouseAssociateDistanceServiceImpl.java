package com.ita.if103java.ims.service.impl;

import com.google.maps.model.DistanceMatrix;
import com.ita.if103java.ims.dto.AssociateAddressDto;
import com.ita.if103java.ims.dto.WarehouseAddressDto;
import com.ita.if103java.ims.dto.WarehouseToAssociateDistanceDto;
import com.ita.if103java.ims.mapper.dto.DistanceMatrixDtoMapper;
import com.ita.if103java.ims.service.DistanceMatrixService;
import com.ita.if103java.ims.service.WarehouseAssociateDistanceService;
import com.ita.if103java.ims.util.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseAssociateDistanceServiceImpl implements WarehouseAssociateDistanceService {
    private final DistanceMatrixService distanceMatrixService;
    private final DistanceMatrixDtoMapper matrixMapper;

    @Autowired
    public WarehouseAssociateDistanceServiceImpl(DistanceMatrixService distanceMatrixService,
                                                 DistanceMatrixDtoMapper matrixMapper) {
        this.distanceMatrixService = distanceMatrixService;
        this.matrixMapper = matrixMapper;
    }

    @Override
    public List<WarehouseToAssociateDistanceDto> getDistances(List<WarehouseAddressDto> warehouseAddresses,
                                                              List<AssociateAddressDto> supplierAddresses,
                                                              List<AssociateAddressDto> clientAddresses) {
        final List<AssociateAddressDto> associateAddresses = ListUtils.concat(supplierAddresses, clientAddresses);
        final DistanceMatrix distanceMatrix = distanceMatrixService.getDistanceMatrix(warehouseAddresses, associateAddresses);
        return matrixMapper.toDtoList(distanceMatrix, warehouseAddresses, associateAddresses);
    }
}
