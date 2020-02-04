package com.ita.if103java.ims.service;

import com.google.maps.model.DistanceMatrix;

import java.util.List;

public interface DistanceMatrixService {
    DistanceMatrix getDistanceMatrix(List<String> origins,
                                     List<String> destinations);
}
