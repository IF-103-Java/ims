package com.ita.if103java.ims.service.impl;

import com.google.maps.model.DistanceMatrix;
import com.ita.if103java.ims.dto.warehouse.advice.Address;
import com.ita.if103java.ims.dto.warehouse.advice.BestWeightedAssociatesDto.WeightedBestAssociateDto;
import com.ita.if103java.ims.dto.warehouse.advice.TopWarehouseAddressDto;
import com.ita.if103java.ims.dto.warehouse.advice.WarehouseToAssociateDistancesDto;
import com.ita.if103java.ims.exception.service.ImpossibleWarehouseAdviceException;
import com.ita.if103java.ims.mapper.dto.WarehouseAdvisorDistanceMatrixDtoMapper;
import com.ita.if103java.ims.service.DistanceMatrixService;
import com.ita.if103java.ims.service.WarehouseBestAssociateDistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ita.if103java.ims.util.ListUtils.isNullOrEmpty;

@Service
public class WarehouseBestAssociateDistanceServiceImpl implements WarehouseBestAssociateDistanceService {
    private final DistanceMatrixService distanceMatrixService;
    private final WarehouseAdvisorDistanceMatrixDtoMapper matrixMapper;

    @Autowired
    public WarehouseBestAssociateDistanceServiceImpl(DistanceMatrixService distanceMatrixService,
                                                     WarehouseAdvisorDistanceMatrixDtoMapper matrixMapper) {
        this.distanceMatrixService = distanceMatrixService;
        this.matrixMapper = matrixMapper;
    }

    @Override
    public WarehouseToAssociateDistancesDto getDistances(List<TopWarehouseAddressDto> warehouses,
                                                         List<WeightedBestAssociateDto> associates) {
        if (isNullOrEmpty(warehouses) || isNullOrEmpty(associates)) {
            throw new ImpossibleWarehouseAdviceException("Your account doesn't have enough valuable info to provide an advice");
        }
        final DistanceMatrix distanceMatrix = distanceMatrixService.getDistanceMatrix(
            buildRequestParams(warehouses, TopWarehouseAddressDto::getAddress),
            buildRequestParams(associates, x -> x.getReference().getReference().getAddress())
        );
        return matrixMapper.toDtoList(distanceMatrix, warehouses, associates);
    }

    private <T> List<String> buildRequestParams(List<T> objects, Function<T, Address> function) {
        return objects.stream()
            .map(function)
            .map(this::buildRequestParam)
            .collect(Collectors.toList());
    }

    private String buildRequestParam(Address address) {
        if (address.getGeo().getLatitude() != null && address.getGeo().getLongitude() != null) {
            return address.getGeo().getLatitude() + "," + address.getGeo().getLongitude();
        }
        return address.getCountry() + "+" + address.getCity();
    }
}
