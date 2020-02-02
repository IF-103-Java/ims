package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dto.WarehouseAdviceDto;
import com.ita.if103java.ims.dto.WarehouseToAssociateDistanceDto;
import com.ita.if103java.ims.dto.warehouse.advice.WarehouseToAssociateDistancesDto;
import com.ita.if103java.ims.service.WarehouseAdvisorCalculationService;
import com.ita.if103java.ims.util.ListUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WarehouseAdvisorCalculationServiceImpl implements WarehouseAdvisorCalculationService {

    @Override
    public List<WarehouseAdviceDto> calculate(WarehouseToAssociateDistancesDto distances) {
        return ListUtils.zipBy(
            getWeightedAvgDistancesGroupedByWarehouses(distances.getSupplierWarehouseDistances()),
            getWeightedAvgDistancesGroupedByWarehouses(distances.getClientWarehouseDistances()),
            (x, y) -> x.getWarehouse().getWarehouseId().equals(y.getWarehouse().getWarehouseId()),
            (x, y) -> new WarehouseAdviceDto(
                x.getWarehouse(),
                x.getTotalWeightedAvgDistance() + y.getTotalWeightedAvgDistance()
            )
        ).stream()
            .sorted(Comparator.comparing(WarehouseAdviceDto::getTotalWeightedAvgDistance))
            .collect(Collectors.toUnmodifiableList());
    }

    private List<WarehouseAdviceDto> getWeightedAvgDistancesGroupedByWarehouses(List<WarehouseToAssociateDistanceDto> distances) {
        return distances.stream()
            .collect(Collectors.groupingBy(WarehouseToAssociateDistanceDto::getWarehouse,
                Collectors.averagingDouble(x -> (1 - x.getAssociate().getWeight()) * x.getDistance().inMeters)))
            .entrySet()
            .stream()
            .map(x -> new WarehouseAdviceDto(x.getKey(), x.getValue()))
            .collect(Collectors.toUnmodifiableList());
    }
}
