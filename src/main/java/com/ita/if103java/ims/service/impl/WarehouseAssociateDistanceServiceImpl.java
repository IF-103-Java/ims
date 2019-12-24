package com.ita.if103java.ims.service.impl;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import com.ita.if103java.ims.dto.AddressDto;
import com.ita.if103java.ims.dto.AssociateAddressDto;
import com.ita.if103java.ims.dto.WarehouseAddressDto;
import com.ita.if103java.ims.dto.WarehouseToAssociateDistanceDto;
import com.ita.if103java.ims.exception.GoogleAPIException;
import com.ita.if103java.ims.mapper.DistanceMatrixMapper;
import com.ita.if103java.ims.service.WarehouseAssociateDistanceService;
import com.ita.if103java.ims.util.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class WarehouseAssociateDistanceServiceImpl implements WarehouseAssociateDistanceService {
    private final GeoApiContext apiContext;
    private final DistanceMatrixMapper matrixMapper;

    @Autowired
    public WarehouseAssociateDistanceServiceImpl(GeoApiContext apiContext, DistanceMatrixMapper matrixMapper) {
        this.apiContext = apiContext;
        this.matrixMapper = matrixMapper;
    }

    @Override
    public List<WarehouseToAssociateDistanceDto> getDistances(List<WarehouseAddressDto> warehouseAddresses,
                                                              List<AssociateAddressDto> supplierAddresses,
                                                              List<AssociateAddressDto> clientAddresses) {
        try {
            final List<AssociateAddressDto> associateAddresses = ListUtils.concat(supplierAddresses, clientAddresses);
            final DistanceMatrix distanceMatrix = DistanceMatrixApi
                .newRequest(apiContext)
                .origins(buildRequestParams(warehouseAddresses))
                .destinations(buildRequestParams(associateAddresses))
                .mode(TravelMode.DRIVING)
                .language("en-US")
                .await();
            return matrixMapper.toDtoList(distanceMatrix, warehouseAddresses, associateAddresses);
        } catch (InterruptedException | ApiException | IOException e) {
            throw new GoogleAPIException("Error when gathering a distanceMatrix", e);
        }
    }

    private String[] buildRequestParams(List<? extends AddressDto> addresses) {
        return addresses.stream()
            .map(this::buildRequestParam)
            .toArray(String[]::new);
    }

    private <T extends AddressDto> String buildRequestParam(T address) {
        if (address.getLatitude() != null && address.getLongitude() != null) {
            return address.getLatitude() + "," + address.getLongitude();
        }
        return address.getCountry() + "+" + address.getCity();
    }
}
