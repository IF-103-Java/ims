package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.AddressDto;
import com.ita.if103java.ims.dto.WarehouseToAssociateDistanceDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.DisabledIf;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisabledIf(value = "true", reason = "Running only local for debug purposes")
public class DistanceMatrixServiceTest {

    @Autowired
    DistanceMatrixService distanceMatrixService;

    @Test
    void testApiCallReturnsCorrectDistanceMatrix() {
        List<AddressDto> warehouseAddresses = Arrays.asList(
            new AddressDto(1L, "USA", "Vancouver", null, null, 49.2827291F, -123.1207375F),
            new AddressDto(2L, "USA", "Las Vegas", null, null, 36.1699412F, -115.1398296F),
            new AddressDto(3L, "USA", "New York", null, null, 40.7127753F, -74.0059728F)
        );

        List<AddressDto> supplierAddresses = Arrays.asList(
            new AddressDto(4L, "USA", "Boston", null, null, 42.3600825F, -71.0588801F),
            new AddressDto(5L, "USA", "Arizona", null, null, 34.0489281F, -111.0937311F),
            new AddressDto(6L, "USA", "San Luis", null, null, 32.4869996F, -114.7821796F)
        );

        List<AddressDto> clientAddresses = Arrays.asList(
            new AddressDto(7L, "USA", "Detroit", null, null, 42.331427F, -83.0457538F),
            new AddressDto(8L, "USA", "San Francisco", null, null, 37.7749295F, -122.4194155F),
            new AddressDto(9L, "USA", "Washington", null, null, 47.7510741F, -120.7401385F)
        );

        final List<WarehouseToAssociateDistanceDto> distances = distanceMatrixService.getDistances(
            warehouseAddresses,
            supplierAddresses,
            clientAddresses
        );
        assertThat(distances.size()).isEqualTo(18);
    }
}
