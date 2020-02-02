package com.ita.if103java.ims.service.impl;

import com.google.common.collect.Iterables;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
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
    public DistanceMatrix getDistanceMatrix(List<String> origins,
                                            List<String> destinations) {
        try {
            return DistanceMatrixApi
                .newRequest(apiContext)
                .origins(Iterables.toArray(origins, String.class))
                .destinations(Iterables.toArray(destinations, String.class))
                .mode(TravelMode.DRIVING)
                .language("en-US")
                .await();
        } catch (ApiException | InterruptedException | IOException e) {
            throw new GoogleAPIException("Error when gathering a distanceMatrix, " +
                "origins=" + origins + " " + "destinations=" + destinations, e);
        }
    }
}
