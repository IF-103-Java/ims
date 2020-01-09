package com.ita.if103java.ims.service.impl;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import com.ita.if103java.ims.dto.AddressDto;
import com.ita.if103java.ims.exception.service.GoogleAPIException;
import com.ita.if103java.ims.service.DistanceMatrixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class DrivingDistanceMatrixServiceImpl implements DistanceMatrixService {
    private final GeoApiContext apiContext;

    @Autowired
    public DrivingDistanceMatrixServiceImpl(GeoApiContext apiContext) {
        this.apiContext = apiContext;
    }

    @Override
    public DistanceMatrix getDistanceMatrix(List<? extends AddressDto> origins,
                                            List<? extends AddressDto> destinations) {
        try {
            return DistanceMatrixApi
                .newRequest(apiContext)
                .origins(buildRequestParams(origins))
                .destinations(buildRequestParams(destinations))
                .mode(TravelMode.DRIVING)
                .language("en-US")
                .await();
        } catch (ApiException | InterruptedException | IOException e) {
            throw new GoogleAPIException("Error when gathering a distanceMatrix, " +
                "origins=" + origins + " " + "destinations=" + destinations, e);
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
