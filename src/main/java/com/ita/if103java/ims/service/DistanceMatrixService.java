package com.ita.if103java.ims.service;

import com.google.maps.model.DistanceMatrix;
import com.ita.if103java.ims.dto.AddressDto;

import java.util.List;

public interface DistanceMatrixService {
    DistanceMatrix getDistances(List<? extends AddressDto> origins,
                                List<? extends AddressDto> destinations);
}
