package com.ita.if103java.ims.service.impl;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.ita.if103java.ims.dto.warehouse.advice.Address;
import com.ita.if103java.ims.service.LocationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class LocationServiceImpl implements LocationService {

    private static final Logger LOGGER = LogManager.getLogger(LocationService.class);
    private final GeoApiContext apiContext;

    @Autowired
    public LocationServiceImpl(GeoApiContext apiContext) {
        this.apiContext = apiContext;
    }

    @Override
    public Address.Geo getLocationByAddress(String address) {

        GeocodingResult[] results = new GeocodingResult[0];

        try {
            results = GeocodingApi.newRequest(apiContext)
                .address(address)
                .await();
        } catch (ApiException | InterruptedException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        if (results.length > 0) {
            LatLng location = results[0].geometry.location;
            return new Address.Geo((float) location.lat, (float) location.lng);
        }
        return new Address.Geo(null, null);
    }
}
