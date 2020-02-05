package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.warehouse.advice.Address;

public interface LocationService {
    Address.Geo getLocationByAddress(String address);
}
