package com.ita.if103java.ims.service.impl;

import com.google.common.collect.Lists;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import com.ita.if103java.ims.dto.AddressDto;
import com.ita.if103java.ims.dto.WarehouseToAssociateDistanceDto;
import com.ita.if103java.ims.service.DistanceMatrixService;
import com.ita.if103java.ims.util.ListUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DistanceMatrixServiceImpl implements DistanceMatrixService {
    private final GeoApiContext apiContext;

    public DistanceMatrixServiceImpl(GeoApiContext apiContext) {
        this.apiContext = apiContext;
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
            return ListUtil.zip(
                Lists.cartesianProduct(Arrays.asList(warehouseAddresses, associateAddresses)),
                Stream.of(distanceMatrix.rows).flatMap(x -> Stream.of(x.elements)).collect(Collectors.toList()),
                (x, y) -> new WarehouseToAssociateDistanceDto(x.get(0), x.get(1), y.distance)
            );
        } catch (InterruptedException | ApiException | IOException e) {
            e.printStackTrace();
        }
        return null;
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
