package com.ita.if103java.ims.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.ita.if103java.ims.service.GeoContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GeoContextServiceImpl implements GeoContextService {
    private final GeoApiContext apiContext;

    @Autowired
    public GeoContextServiceImpl(GeoApiContext apiContext) {
        this.apiContext = apiContext;
    }

    @Override
    public String getGeoFromAddress(String address) {

        GeocodingResult[] results = new GeocodingResult[0];

        try {
            results = GeocodingApi.newRequest(apiContext)
                .address(address)
                .await();
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }

        String result = results[0].geometry.location.toString();

        return result;
    }
}
