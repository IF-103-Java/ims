package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.AssociateAddressDto;
import com.ita.if103java.ims.dto.WarehouseAddressDto;
import com.ita.if103java.ims.dto.WarehouseToAssociateDistanceDto;
import com.ita.if103java.ims.entity.AssociateType;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.DisabledIf;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.allOf;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisabledIf(value = "true", reason = "Running only local for debug purposes")
public class WarehouseAssociateDistanceServiceTest {

    @Autowired
    WarehouseAssociateDistanceService distanceMatrixService;

    @Test
    void testApiCallReturnsCorrectDistanceMatrix() {
        final List<WarehouseAddressDto> warehouseAddresses = Arrays.asList(
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

        final List<AssociateAddressDto> supplierAddresses = Arrays.asList(
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

        final List<AssociateAddressDto> clientAddresses = Arrays.asList(
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
//          W1
        final WarehouseToAssociateDistanceDto vancouverBoston = distances.get(0);
        final WarehouseToAssociateDistanceDto vancouverArizona = distances.get(1);
        final WarehouseToAssociateDistanceDto vancouverSanLuis = distances.get(2);
        final WarehouseToAssociateDistanceDto vancouverDetroit = distances.get(3);
        final WarehouseToAssociateDistanceDto vancouverSanFrancisco = distances.get(4);
        final WarehouseToAssociateDistanceDto vancouverWashington = distances.get(5);
//          W2
        final WarehouseToAssociateDistanceDto lasVegasBoston = distances.get(6);
        final WarehouseToAssociateDistanceDto lasVegasArizona = distances.get(7);
        final WarehouseToAssociateDistanceDto lasVegasSanLuis = distances.get(8);
        final WarehouseToAssociateDistanceDto lasVegasDetroit = distances.get(9);
        final WarehouseToAssociateDistanceDto lasVegasSanFrancisco = distances.get(10);
        final WarehouseToAssociateDistanceDto lasVegasWashington = distances.get(11);
//          W3
        final WarehouseToAssociateDistanceDto newYorkBoston = distances.get(12);
        final WarehouseToAssociateDistanceDto newYorkArizona = distances.get(13);
        final WarehouseToAssociateDistanceDto newYorkSanLuis = distances.get(14);
        final WarehouseToAssociateDistanceDto newYorkDetroit = distances.get(15);
        final WarehouseToAssociateDistanceDto newYorkSanFrancisco = distances.get(16);
        final WarehouseToAssociateDistanceDto newYorkWashington = distances.get(17);

//        W1
        assertThat(vancouverBoston).has(allOf(
            warehouseAddress("Vancouver"),
            associateAddress("Boston"),
            associateAddressType(AssociateType.SUPPLIER),
            distance("5,129 km")
        ));
        assertThat(vancouverArizona).has(allOf(
            warehouseAddress("Vancouver"),
            associateAddress("Arizona"),
            associateAddressType(AssociateType.SUPPLIER),
            distance("2,587 km")
        ));
        assertThat(vancouverSanLuis).has(allOf(
            warehouseAddress("Vancouver"),
            associateAddress("San Luis"),
            associateAddressType(AssociateType.SUPPLIER),
            distance("2,529 km")
        ));
        assertThat(vancouverDetroit).has(allOf(
            warehouseAddress("Vancouver"),
            associateAddress("Detroit"),
            associateAddressType(AssociateType.CLIENT),
            distance("3,999 km")
        ));
        assertThat(vancouverSanFrancisco).has(allOf(
            warehouseAddress("Vancouver"),
            associateAddress("San Francisco"),
            associateAddressType(AssociateType.CLIENT),
            distance("1,528 km")
        ));
        assertThat(vancouverWashington).has(allOf(
            warehouseAddress("Vancouver"),
            associateAddress("Washington"),
            associateAddressType(AssociateType.CLIENT),
            distance("320 km")
        ));
//        W2
        assertThat(lasVegasBoston).has(allOf(
            warehouseAddress("Las Vegas"),
            associateAddress("Boston"),
            associateAddressType(AssociateType.SUPPLIER),
            distance("4,370 km")
        ));
        assertThat(lasVegasArizona).has(allOf(
            warehouseAddress("Las Vegas"),
            associateAddress("Arizona"),
            associateAddressType(AssociateType.SUPPLIER),
            distance("720 km")
        ));
        assertThat(lasVegasSanLuis).has(allOf(
            warehouseAddress("Las Vegas"),
            associateAddress("San Luis"),
            associateAddressType(AssociateType.SUPPLIER),
            distance("508 km")
        ));
        assertThat(lasVegasDetroit).has(allOf(
            warehouseAddress("Las Vegas"),
            associateAddress("Detroit"),
            associateAddressType(AssociateType.CLIENT),
            distance("3,239 km")
        ));
        assertThat(lasVegasSanFrancisco).has(allOf(
            warehouseAddress("Las Vegas"),
            associateAddress("San Francisco"),
            associateAddressType(AssociateType.CLIENT),
            distance("917 km")
        ));
        assertThat(lasVegasWashington).has(allOf(
            warehouseAddress("Las Vegas"),
            associateAddress("Washington"),
            associateAddressType(AssociateType.CLIENT),
            distance("1,757 km")
        ));
//        W3
        assertThat(newYorkBoston).has(allOf(
            warehouseAddress("New York"),
            associateAddress("Boston"),
            associateAddressType(AssociateType.SUPPLIER),
            distance("346 km")
        ));
        assertThat(newYorkArizona).has(allOf(
            warehouseAddress("New York"),
            associateAddress("Arizona"),
            associateAddressType(AssociateType.SUPPLIER),
            distance("3,751 km")
        ));
        assertThat(newYorkSanLuis).has(allOf(
            warehouseAddress("New York"),
            associateAddress("San Luis"),
            associateAddressType(AssociateType.SUPPLIER),
            distance("4,199 km")
        ));
        assertThat(newYorkDetroit).has(allOf(
            warehouseAddress("New York"),
            associateAddress("Detroit"),
            associateAddressType(AssociateType.CLIENT),
            distance("989 km")
        ));
        assertThat(newYorkSanFrancisco).has(allOf(
            warehouseAddress("New York"),
            associateAddress("San Francisco"),
            associateAddressType(AssociateType.CLIENT),
            distance("4,671 km")
        ));
        assertThat(newYorkWashington).has(allOf(
            warehouseAddress("New York"),
            associateAddress("Washington"),
            associateAddressType(AssociateType.CLIENT),
            distance("4,492 km")
        ));
    }

    private Condition<WarehouseToAssociateDistanceDto> warehouseAddress(String city) {
        return new Condition<>(x -> x.getWarehouseAddress().getCity().equals(city), "warehouse address");
    }

    private Condition<WarehouseToAssociateDistanceDto> associateAddress(String city) {
        return new Condition<>(
            x -> x.getAssociateAddress().getCity().equals(city),
            "associate address"
        );
    }

    private Condition<WarehouseToAssociateDistanceDto> associateAddressType(AssociateType associateType) {
        return new Condition<>(
            x -> x.getAssociateAddress().getAssociateType().equals(associateType),
            "associate address type"
        );
    }

    private Condition<WarehouseToAssociateDistanceDto> distance(String humanReadableDistance) {
        return new Condition<>(
            x -> x.getDistance().humanReadable.equals(humanReadableDistance),
            "human readable distance"
        );
    }
}
