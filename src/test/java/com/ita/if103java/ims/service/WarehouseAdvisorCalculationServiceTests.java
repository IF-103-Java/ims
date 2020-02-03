package com.ita.if103java.ims.service;

import com.google.maps.model.Distance;
import com.google.maps.model.DistanceMatrixElementStatus;
import com.ita.if103java.ims.dto.warehouse.advice.TopWarehouseAddressDto;
import com.ita.if103java.ims.dto.warehouse.advice.advice.WarehouseAdviceDto;
import com.ita.if103java.ims.dto.warehouse.advice.associate.Associate;
import com.ita.if103java.ims.dto.warehouse.advice.associate.BestAssociateDto;
import com.ita.if103java.ims.dto.warehouse.advice.associate.BestWeightedAssociateDto;
import com.ita.if103java.ims.dto.warehouse.advice.distance.WarehouseToAssociateDistanceDto;
import com.ita.if103java.ims.dto.warehouse.advice.distance.WarehouseToAssociateDistancesDto;
import com.ita.if103java.ims.entity.AssociateType;
import com.ita.if103java.ims.service.impl.WarehouseAdvisorCalculationServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

public class WarehouseAdvisorCalculationServiceTests {

    private final WarehouseAdvisorCalculationService service = new WarehouseAdvisorCalculationServiceImpl();

    @Test
    public void testAvgDistanceCalculationsWithoutWeights() {
        List<WarehouseToAssociateDistanceDto> supplierWarehouseDistances = Arrays.asList(
            associateDistance(1L, 1L, AssociateType.SUPPLIER, 150, 0),
            associateDistance(1L, 2L, AssociateType.SUPPLIER, 25, 0),
            associateDistance(1L, 3L, AssociateType.SUPPLIER, 200, 0),
            associateDistance(1L, 4L, AssociateType.SUPPLIER, 50, 0),
            associateDistance(1L, 5L, AssociateType.SUPPLIER, 100, 0),
            associateDistance(2L, 1L, AssociateType.SUPPLIER, 75, 0),
            associateDistance(2L, 2L, AssociateType.SUPPLIER, 100, 0),
            associateDistance(2L, 3L, AssociateType.SUPPLIER, 150, 0),
            associateDistance(2L, 4L, AssociateType.SUPPLIER, 50, 0),
            associateDistance(2L, 5L, AssociateType.SUPPLIER, 200, 0),
            associateDistance(3L, 1L, AssociateType.SUPPLIER, 25, 0),
            associateDistance(3L, 2L, AssociateType.SUPPLIER, 50, 0),
            associateDistance(3L, 3L, AssociateType.SUPPLIER, 100, 0),
            associateDistance(3L, 4L, AssociateType.SUPPLIER, 150, 0),
            associateDistance(3L, 5L, AssociateType.SUPPLIER, 200, 0)
        );
        List<WarehouseToAssociateDistanceDto> clientWarehouseDistances = Arrays.asList(
            associateDistance(4L, 1L, AssociateType.CLIENT, 25, 0),
            associateDistance(4L, 2L, AssociateType.CLIENT, 100, 0),
            associateDistance(4L, 3L, AssociateType.CLIENT, 75, 0),
            associateDistance(4L, 4L, AssociateType.CLIENT, 150, 0),
            associateDistance(4L, 5L, AssociateType.CLIENT, 50, 0),
            associateDistance(5L, 1L, AssociateType.CLIENT, 150, 0),
            associateDistance(5L, 2L, AssociateType.CLIENT, 200, 0),
            associateDistance(5L, 3L, AssociateType.CLIENT, 25, 0),
            associateDistance(5L, 4L, AssociateType.CLIENT, 50, 0),
            associateDistance(5L, 5L, AssociateType.CLIENT, 100, 0),
            associateDistance(6L, 1L, AssociateType.CLIENT, 100, 0),
            associateDistance(6L, 2L, AssociateType.CLIENT, 50, 0),
            associateDistance(6L, 3L, AssociateType.CLIENT, 150, 0),
            associateDistance(6L, 4L, AssociateType.CLIENT, 25, 0),
            associateDistance(6L, 5L, AssociateType.CLIENT, 75, 0)
        );

        WarehouseToAssociateDistancesDto distances = new WarehouseToAssociateDistancesDto(
            supplierWarehouseDistances,
            clientWarehouseDistances
        );

        final List<WarehouseAdviceDto> advices = service.calculate(distances);
        assertThat(advices.size()).isEqualTo(5);
        assertAdvice(advices.get(0), 4L, 158.33);
        assertAdvice(advices.get(1), 1L, 174.99);
        assertAdvice(advices.get(2), 2L, 174.99);
        assertAdvice(advices.get(3), 3L, 233.33);
        assertAdvice(advices.get(4), 5L, 241.66);
    }

    @Test
    public void testAvgDistanceCalculationsWithWeights() {
         final List<WarehouseToAssociateDistanceDto> supplierWarehouseDistances = Arrays.asList(
            associateDistance(1L, 1L, AssociateType.SUPPLIER, 5129, 0.47),
            associateDistance(1L, 2L, AssociateType.SUPPLIER, 4370, 0.47),
            associateDistance(1L, 3L, AssociateType.SUPPLIER, 346, 0.47),
            associateDistance(2L, 1L, AssociateType.SUPPLIER, 2587, 0.13),
            associateDistance(2L, 2L, AssociateType.SUPPLIER, 720, 0.13),
            associateDistance(2L, 3L, AssociateType.SUPPLIER, 3751, 0.13),
            associateDistance(3L, 1L, AssociateType.SUPPLIER, 2529, 0.40),
            associateDistance(3L, 2L, AssociateType.SUPPLIER, 508, 0.40),
            associateDistance(3L, 3L, AssociateType.SUPPLIER, 4199, 0.40)
         );

         final List<WarehouseToAssociateDistanceDto> clientWarehouseDistances = Arrays.asList(
            associateDistance(4L, 1L, AssociateType.CLIENT, 3999, 0.33),
            associateDistance(4L, 2L, AssociateType.CLIENT, 3239, 0.33),
            associateDistance(4L, 3L, AssociateType.CLIENT, 989, 0.33),
            associateDistance(5L, 1L, AssociateType.CLIENT, 1528, 0.37),
            associateDistance(5L, 2L, AssociateType.CLIENT, 917, 0.37),
            associateDistance(5L, 3L, AssociateType.CLIENT, 4671, 0.37),
            associateDistance(6L, 1L, AssociateType.CLIENT, 320, 0.30),
            associateDistance(6L, 2L, AssociateType.CLIENT, 1757, 0.30),
            associateDistance(6L, 3L, AssociateType.CLIENT, 4492, 0.30)
         );

         WarehouseToAssociateDistancesDto distances = new WarehouseToAssociateDistancesDto(
            supplierWarehouseDistances,
            clientWarehouseDistances
        );

        final List<WarehouseAdviceDto> advices = service.calculate(distances);
        assertThat(advices.size()).isEqualTo(3);
        assertAdvice(advices.get(0), 2L, 2408.34);
        assertAdvice(advices.get(1), 1L, 3450.80);
        assertAdvice(advices.get(2), 3L, 4238.63);
    }

    private void assertAdvice(WarehouseAdviceDto advice,
                              Long expectedWarehouseId,
                              Double expectedAvgDistance) {
        assertThat(advice.getWarehouse().getId()).isEqualTo(expectedWarehouseId);
        assertThat(advice.getTotalWeightedAvgDistance()).isCloseTo(expectedAvgDistance, offset(0.01));
    }

    private WarehouseToAssociateDistanceDto associateDistance(Long associateId,
                                                              Long warehouseId,
                                                              AssociateType associateType,
                                                              long distance,
                                                              double weight) {
        final Associate associate = new Associate(associateId, null, null, associateType);
        final BestAssociateDto bestAssociateDto = new BestAssociateDto(associate, null);
        return new WarehouseToAssociateDistanceDto(
            new TopWarehouseAddressDto(warehouseId, null, null),
            new BestWeightedAssociateDto(bestAssociateDto, weight),
            distance(distance),
            DistanceMatrixElementStatus.OK
        );
    }

    private Distance distance(long distance) {
        final Distance d = new Distance();
        d.inMeters = distance;
        return d;
    }
}
