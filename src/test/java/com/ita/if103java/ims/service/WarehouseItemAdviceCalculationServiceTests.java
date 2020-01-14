package com.ita.if103java.ims.service;

import com.google.maps.model.Distance;
import com.google.maps.model.DistanceMatrixElementStatus;
import com.ita.if103java.ims.dto.AssociateAddressDto;
import com.ita.if103java.ims.dto.WarehouseAddressDto;
import com.ita.if103java.ims.dto.WarehouseIdAdviceDto;
import com.ita.if103java.ims.dto.WarehouseToAssociateDistanceDto;
import com.ita.if103java.ims.dto.WeightAssociateDto;
import com.ita.if103java.ims.entity.AssociateType;
import com.ita.if103java.ims.service.impl.WarehouseItemAdviceCalculationServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

public class WarehouseItemAdviceCalculationServiceTests {
    private final WarehouseItemAdviceCalculationServiceImpl service = new WarehouseItemAdviceCalculationServiceImpl();

    @Test
    public void testAvgDistanceCalculations() {
        final List<WeightAssociateDto> associates = Arrays.asList(
            new WeightAssociateDto(1L, 0.0),
            new WeightAssociateDto(2L, 0.0),
            new WeightAssociateDto(3L, 0.0)
        );

        final List<WeightAssociateDto> clients = Arrays.asList(
            new WeightAssociateDto(4L, 0.0),
            new WeightAssociateDto(5L, 0.0),
            new WeightAssociateDto(6L, 0.0)
        );

        final List<WarehouseToAssociateDistanceDto> distances = Arrays.asList(
            supplierDistance(1L, 1L, 150),
            supplierDistance(1L, 2L, 25),
            supplierDistance(1L, 3L, 200),
            supplierDistance(1L, 4L, 50),
            supplierDistance(1L, 5L, 100),
//                ----------------------------------------------------------------------------------------
            supplierDistance(2L, 1L, 75),
            supplierDistance(2L, 2L, 100),
            supplierDistance(2L, 3L, 150),
            supplierDistance(2L, 4L, 50),
            supplierDistance(2L, 5L, 200),
//                ----------------------------------------------------------------------------------------
            supplierDistance(3L, 1L, 25),
            supplierDistance(3L, 2L, 50),
            supplierDistance(3L, 3L, 100),
            supplierDistance(3L, 4L, 150),
            supplierDistance(3L, 5L, 200),
//                ----------------------------------------------------------------------------------------
            clientDistance(4L, 1L, 25),
            clientDistance(4L, 2L, 100),
            clientDistance(4L, 3L, 75),
            clientDistance(4L, 4L, 150),
            clientDistance(4L, 5L, 50),
//                ----------------------------------------------------------------------------------------
            clientDistance(5L, 1L, 150),
            clientDistance(5L, 2L, 200),
            clientDistance(5L, 3L, 25),
            clientDistance(5L, 4L, 50),
            clientDistance(5L, 5L, 100),
//                ----------------------------------------------------------------------------------------
            clientDistance(6L, 1L, 100),
            clientDistance(6L, 2L, 50),
            clientDistance(6L, 3L, 150),
            clientDistance(6L, 4L, 25),
            clientDistance(6L, 5L, 75)
        );

        final List<WarehouseIdAdviceDto> advices = service.calculate(associates, clients, distances);
        assertThat(advices.size()).isEqualTo(5);
        assertAdvice(advices.get(0), 4L, 158.33);
        assertAdvice(advices.get(1), 1L, 174.99);
        assertAdvice(advices.get(2), 2L, 174.99);
        assertAdvice(advices.get(3), 3L, 233.33);
        assertAdvice(advices.get(4), 5L, 241.66);
    }

    private void assertAdvice(WarehouseIdAdviceDto advice,
                              Long expectedWarehouseId,
                              Double expectedAvgDistance) {
        assertThat(advice.getWarehouseId()).isEqualTo(expectedWarehouseId);
        assertThat(advice.getWeightedAvgDistance()).isCloseTo(expectedAvgDistance, offset(0.01));
    }

    private WarehouseToAssociateDistanceDto clientDistance(Long clientId, Long warehouseId, long distance) {
        return new WarehouseToAssociateDistanceDto(
            warehouseAddress(warehouseId),
            associateAddress(clientId, AssociateType.CLIENT),
            distance(distance),
            DistanceMatrixElementStatus.OK
        );
    }

    private WarehouseToAssociateDistanceDto supplierDistance(Long supplierId, Long warehouseId, long distance) {
        return new WarehouseToAssociateDistanceDto(
            warehouseAddress(warehouseId),
            associateAddress(supplierId, AssociateType.SUPPLIER),
            distance(distance),
            DistanceMatrixElementStatus.OK
        );
    }

    private WarehouseAddressDto warehouseAddress(Long warehouseId) {
        return new WarehouseAddressDto(null, null, null, null, null, null, null, warehouseId);
    }

    private AssociateAddressDto associateAddress(Long associateId, AssociateType associateType) {
        return new AssociateAddressDto(null, null, null, null, null, null, null, associateId, associateType);
    }

    private Distance distance(long distance) {
        final Distance d = new Distance();
        d.inMeters = distance;
        return d;
    }
}
