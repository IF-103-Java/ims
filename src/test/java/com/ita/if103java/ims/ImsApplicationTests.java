package com.ita.if103java.ims;

import com.ita.if103java.ims.dto.warehouse.advice.Address;
import com.ita.if103java.ims.service.LocationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ImsApplicationTests {

    @Autowired
    LocationService service;

    @Test
    void contextLoads() {

        Address.Geo geo = service.getLocationByAddress("450 Loeprich Center Fenwick");

        System.out.println(geo);
    }

}
