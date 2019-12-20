package com.ita.if103java.ims.service.impl;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import com.ita.if103java.ims.dto.AddressDto;
import com.ita.if103java.ims.dto.WarehouseToAssociateDistanceDto;
import com.ita.if103java.ims.exception.GoogleAPIException;
import com.ita.if103java.ims.mapper.DistanceMatrixMapper;
import com.ita.if103java.ims.service.DistanceMatrixService;
import com.ita.if103java.ims.util.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class DistanceMatrixServiceImpl implements DistanceMatrixService {
    private final GeoApiContext apiContext;
    private final DistanceMatrixMapper matrixMapper;

    @Autowired
    public DistanceMatrixServiceImpl(GeoApiContext apiContext, DistanceMatrixMapper matrixMapper) {
        this.apiContext = apiContext;
        this.matrixMapper = matrixMapper;
    }

    @Override
    public List<WarehouseToAssociateDistanceDto> getDistances(List<AddressDto> warehouseAddresses,
                                                              List<AddressDto> supplierAddresses,
                                                              List<AddressDto> clientAddresses) {
        try {
            final List<AddressDto> associateAddresses = ListUtil.concat(supplierAddresses, clientAddresses);
            final DistanceMatrix distanceMatrix = DistanceMatrixApi
                .newRequest(apiContext)
                .origins(buildGeoRequestParams(warehouseAddresses))
                .destinations(buildGeoRequestParams(associateAddresses))
                .mode(TravelMode.DRIVING)
                .language("en-US")
                .await();
            return matrixMapper.toDtoList(distanceMatrix, warehouseAddresses, associateAddresses);
        } catch (InterruptedException | ApiException | IOException e) {
            throw new GoogleAPIException("Error when gathering a distanceMatrix", e);
        }
    }

    private String[] buildGeoRequestParams(List<AddressDto> addresses) {
        return addresses.stream()
            .map(x -> {
                if (x.getLatitude() != null && x.getLongitude() != null) {
                    return x.getLatitude() + "," + x.getLongitude();
                }
                return x.getCountry() + "+" + x.getCity();
            })
            .toArray(String[]::new);
    }
}
