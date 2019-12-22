package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.AssociateAddressDto;
import com.ita.if103java.ims.dto.WarehouseAddressDto;
import com.ita.if103java.ims.dto.WarehouseToAssociateDistanceDto;
import com.ita.if103java.ims.entity.AssociateType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.DisabledIf;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisabledIf(value = "true", reason = "Running only local for debug purposes")
public class WarehouseAssociateDistanceServiceTest {

    @Autowired
    WarehouseAssociateDistanceService distanceMatrixService;

    @Test
    void testApiCallReturnsCorrectDistanceMatrix() {
        List<WarehouseAddressDto> warehouseAddresses = Arrays.asList(
            new WarehouseAddressDto(
                1L, "USA", "Vancouver", null, null,
                49.2827291F, -123.1207375F, 1L
            ),
            new WarehouseAddressDto(
                2L, "USA", "Las Vegas", null, null,
                36.1699412F, -115.1398296F, 2L
            ),
            new WarehouseAddressDto(
                3L, "USA", "New York", null, null,
                40.7127753F, -74.0059728F, 3L
            )
        );

        List<AssociateAddressDto> supplierAddresses = Arrays.asList(
            new AssociateAddressDto(
                4L, "USA", "Boston", null, null,
                42.3600825F, -71.0588801F, 1L, AssociateType.SUPPLIER
            ),
            new AssociateAddressDto(
                5L, "USA", "Arizona", null, null,
                34.0489281F, -111.0937311F, 2L, AssociateType.SUPPLIER
            ),
            new AssociateAddressDto(
                6L, "USA", "San Luis", null, null,
                32.4869996F, -114.7821796F, 3L, AssociateType.SUPPLIER
            )
        );

        List<AssociateAddressDto> clientAddresses = Arrays.asList(
            new AssociateAddressDto(
                7L, "USA", "Detroit", null, null,
                42.331427F, -83.0457538F, 4L, AssociateType.CLIENT
            ),
            new AssociateAddressDto(
                8L, "USA", "San Francisco", null, null,
                37.7749295F, -122.4194155F, 5L, AssociateType.CLIENT
            ),
            new AssociateAddressDto(
                9L, "USA", "Washington", null, null,
                47.7510741F, -120.7401385F, 6L, AssociateType.CLIENT
            )
        );

        final List<WarehouseToAssociateDistanceDto> distances = distanceMatrixService.getDistances(
            warehouseAddresses,
            supplierAddresses,
            clientAddresses
        );
        assertThat(distances.size()).isEqualTo(18);
        // TODO: 22.12.19 Add distances testing
    }
}
