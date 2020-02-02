package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.warehouse.advice.WarehouseToAssociateDistanceDto;
import com.ita.if103java.ims.dto.warehouse.advice.Address;
import com.ita.if103java.ims.dto.warehouse.advice.Address.Geo;
import com.ita.if103java.ims.dto.warehouse.advice.BestAssociateDto;
import com.ita.if103java.ims.dto.warehouse.advice.BestAssociateDto.Associate;
import com.ita.if103java.ims.dto.warehouse.advice.BestAssociatesDto.WeightedBestAssociateDto;
import com.ita.if103java.ims.dto.warehouse.advice.TopWarehouseAddressDto;
import com.ita.if103java.ims.dto.warehouse.advice.WarehouseToAssociateDistancesDto;
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
public class WarehouseBestAssociateDistanceServiceTest {

    @Autowired
    WarehouseBestAssociateDistanceService warehouseBestAssociateDistanceService;

    @Test
    void testApiCallReturnsCorrectDistanceMatrix() {
        final List<TopWarehouseAddressDto> topWarehouses = Arrays.asList(
            new TopWarehouseAddressDto(1L, "W1", new Address("USA", "Vancouver", null, new Geo(49.2827291F, -123.1207375F))),
            new TopWarehouseAddressDto(2L, "W2", new Address("USA", "Las Vegas", null, new Geo(36.1699412F, -115.1398296F))),
            new TopWarehouseAddressDto(3L, "W3", new Address("USA", "New York", null, new Geo(40.7127753F, -74.0059728F)))
        );

        final List<WeightedBestAssociateDto> associates = Arrays.asList(
            new WeightedBestAssociateDto(new BestAssociateDto(new Associate(1L, "S1", new Address("USA", "Boston", null, new Geo(42.3600825F, -71.0588801F)), AssociateType.SUPPLIER), 10L), 0.2),
            new WeightedBestAssociateDto(new BestAssociateDto(new Associate(2L, "S2", new Address("USA", "Arizona", null, new Geo(34.0489281F, -111.0937311F)), AssociateType.SUPPLIER), 20L), 0.3),
            new WeightedBestAssociateDto(new BestAssociateDto(new Associate(3L, "S3", new Address("USA", "San Luis", null, new Geo(32.4869996F, -114.7821796F)), AssociateType.SUPPLIER), 30L), 0.1),
            new WeightedBestAssociateDto(new BestAssociateDto(new Associate(4L, "C1", new Address("USA", "Detroit", null, new Geo(42.331427F, -83.0457538F)), AssociateType.CLIENT), 10L), 0.2),
            new WeightedBestAssociateDto(new BestAssociateDto(new Associate(5L, "C2", new Address("USA", "San Francisco", null, new Geo(37.7749295F, -122.4194155F)), AssociateType.CLIENT), 50L), 0.1),
            new WeightedBestAssociateDto(new BestAssociateDto(new Associate(6L, "C3", new Address("USA", "Washington", null, new Geo(47.7510741F, -120.7401385F)), AssociateType.CLIENT), 60L), 0.5)
        );

        final WarehouseToAssociateDistancesDto dto = warehouseBestAssociateDistanceService.getDistances(topWarehouses, associates);
        final List<WarehouseToAssociateDistanceDto> clientDistances = dto.getClientWarehouseDistances();
        final List<WarehouseToAssociateDistanceDto> supplierDistances = dto.getSupplierWarehouseDistances();
        assertThat(clientDistances.size()).isEqualTo(9);
        assertThat(supplierDistances.size()).isEqualTo(9);
//          W1
        final WarehouseToAssociateDistanceDto vancouverBoston = supplierDistances.get(0);
        final WarehouseToAssociateDistanceDto vancouverArizona = supplierDistances.get(1);
        final WarehouseToAssociateDistanceDto vancouverSanLuis = supplierDistances.get(2);
        final WarehouseToAssociateDistanceDto vancouverDetroit = clientDistances.get(0);
        final WarehouseToAssociateDistanceDto vancouverSanFrancisco = clientDistances.get(1);
        final WarehouseToAssociateDistanceDto vancouverWashington = clientDistances.get(2);
//          W2
        final WarehouseToAssociateDistanceDto lasVegasBoston = supplierDistances.get(3);
        final WarehouseToAssociateDistanceDto lasVegasArizona = supplierDistances.get(4);
        final WarehouseToAssociateDistanceDto lasVegasSanLuis = supplierDistances.get(5);
        final WarehouseToAssociateDistanceDto lasVegasDetroit = clientDistances.get(3);
        final WarehouseToAssociateDistanceDto lasVegasSanFrancisco = clientDistances.get(4);
        final WarehouseToAssociateDistanceDto lasVegasWashington = clientDistances.get(5);
//          W3
        final WarehouseToAssociateDistanceDto newYorkBoston = supplierDistances.get(6);
        final WarehouseToAssociateDistanceDto newYorkArizona = supplierDistances.get(7);
        final WarehouseToAssociateDistanceDto newYorkSanLuis = supplierDistances.get(8);
        final WarehouseToAssociateDistanceDto newYorkDetroit = clientDistances.get(6);
        final WarehouseToAssociateDistanceDto newYorkSanFrancisco = clientDistances.get(7);
        final WarehouseToAssociateDistanceDto newYorkWashington = clientDistances.get(8);

//        W1
        assertThat(vancouverBoston).has(allOf(
            warehouseAddressCity("Vancouver"),
            associateAddressCity("Boston"),
            associateAddressType(AssociateType.SUPPLIER),
            distance("5,129 km")
        ));
        assertThat(vancouverArizona).has(allOf(
            warehouseAddressCity("Vancouver"),
            associateAddressCity("Arizona"),
            associateAddressType(AssociateType.SUPPLIER),
            distance("2,587 km")
        ));
        assertThat(vancouverSanLuis).has(allOf(
            warehouseAddressCity("Vancouver"),
            associateAddressCity("San Luis"),
            associateAddressType(AssociateType.SUPPLIER),
            distance("2,543 km")
        ));
        assertThat(vancouverDetroit).has(allOf(
            warehouseAddressCity("Vancouver"),
            associateAddressCity("Detroit"),
            associateAddressType(AssociateType.CLIENT),
            distance("3,999 km")
        ));
        assertThat(vancouverSanFrancisco).has(allOf(
            warehouseAddressCity("Vancouver"),
            associateAddressCity("San Francisco"),
            associateAddressType(AssociateType.CLIENT),
            distance("1,528 km")
        ));
        assertThat(vancouverWashington).has(allOf(
            warehouseAddressCity("Vancouver"),
            associateAddressCity("Washington"),
            associateAddressType(AssociateType.CLIENT),
            distance("320 km")
        ));
//        W2
        assertThat(lasVegasBoston).has(allOf(
            warehouseAddressCity("Las Vegas"),
            associateAddressCity("Boston"),
            associateAddressType(AssociateType.SUPPLIER),
            distance("4,370 km")
        ));
        assertThat(lasVegasArizona).has(allOf(
            warehouseAddressCity("Las Vegas"),
            associateAddressCity("Arizona"),
            associateAddressType(AssociateType.SUPPLIER),
            distance("720 km")
        ));
        assertThat(lasVegasSanLuis).has(allOf(
            warehouseAddressCity("Las Vegas"),
            associateAddressCity("San Luis"),
            associateAddressType(AssociateType.SUPPLIER),
            distance("508 km")
        ));
        assertThat(lasVegasDetroit).has(allOf(
            warehouseAddressCity("Las Vegas"),
            associateAddressCity("Detroit"),
            associateAddressType(AssociateType.CLIENT),
            distance("3,239 km")
        ));
        assertThat(lasVegasSanFrancisco).has(allOf(
            warehouseAddressCity("Las Vegas"),
            associateAddressCity("San Francisco"),
            associateAddressType(AssociateType.CLIENT),
            distance("915 km")
        ));
        assertThat(lasVegasWashington).has(allOf(
            warehouseAddressCity("Las Vegas"),
            associateAddressCity("Washington"),
            associateAddressType(AssociateType.CLIENT),
            distance("1,757 km")
        ));
//        W3
        assertThat(newYorkBoston).has(allOf(
            warehouseAddressCity("New York"),
            associateAddressCity("Boston"),
            associateAddressType(AssociateType.SUPPLIER),
            distance("346 km")
        ));
        assertThat(newYorkArizona).has(allOf(
            warehouseAddressCity("New York"),
            associateAddressCity("Arizona"),
            associateAddressType(AssociateType.SUPPLIER),
            distance("3,751 km")
        ));
        assertThat(newYorkSanLuis).has(allOf(
            warehouseAddressCity("New York"),
            associateAddressCity("San Luis"),
            associateAddressType(AssociateType.SUPPLIER),
            distance("4,199 km")
        ));
        assertThat(newYorkDetroit).has(allOf(
            warehouseAddressCity("New York"),
            associateAddressCity("Detroit"),
            associateAddressType(AssociateType.CLIENT),
            distance("989 km")
        ));
        assertThat(newYorkSanFrancisco).has(allOf(
            warehouseAddressCity("New York"),
            associateAddressCity("San Francisco"),
            associateAddressType(AssociateType.CLIENT),
            distance("4,671 km")
        ));
        assertThat(newYorkWashington).has(allOf(
            warehouseAddressCity("New York"),
            associateAddressCity("Washington"),
            associateAddressType(AssociateType.CLIENT),
            distance("4,492 km")
        ));
    }

    private Condition<WarehouseToAssociateDistanceDto> warehouseAddressCity(String city) {
        return new Condition<>(
            x -> x.getWarehouse().getAddress().getCity().equals(city),
            "warehouse address city"
        );
    }

    private Condition<WarehouseToAssociateDistanceDto> associateAddressCity(String city) {
        return new Condition<>(
            x -> x.getAssociate().getReference().getReference().getAddress().getCity().equals(city),
            "associate address city"
        );
    }

    private Condition<WarehouseToAssociateDistanceDto> associateAddressType(AssociateType associateType) {
        return new Condition<>(
            x -> x.getAssociate().getReference().getReference().getType().equals(associateType),
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
