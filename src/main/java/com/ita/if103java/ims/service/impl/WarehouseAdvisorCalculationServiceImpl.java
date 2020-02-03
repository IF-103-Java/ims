package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dto.warehouse.advice.advice.WarehouseAdviceDto;
import com.ita.if103java.ims.dto.warehouse.advice.distance.WarehouseToAssociateDistanceDto;
import com.ita.if103java.ims.dto.warehouse.advice.distance.WarehouseToAssociateDistancesDto;
import com.ita.if103java.ims.exception.service.ImpossibleWarehouseAdviceException;
import com.ita.if103java.ims.service.WarehouseAdvisorCalculationService;
import com.ita.if103java.ims.util.ListUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.ita.if103java.ims.util.ListUtils.isNullOrEmpty;

@Service
public class WarehouseAdvisorCalculationServiceImpl implements WarehouseAdvisorCalculationService {

    @Override
    public List<WarehouseAdviceDto> calculate(WarehouseToAssociateDistancesDto distances) {
        final List<WarehouseToAssociateDistanceDto> supplierWarehouseDistances = distances.getSupplierWarehouseDistances();
        final List<WarehouseToAssociateDistanceDto> clientWarehouseDistances = distances.getClientWarehouseDistances();
        if (isNullOrEmpty(supplierWarehouseDistances) && isNullOrEmpty(clientWarehouseDistances)) {
            throw new ImpossibleWarehouseAdviceException("Your account doesn't have enough valuable info to provide an advice");
        }
        else if (isNullOrEmpty(supplierWarehouseDistances)) {
            return calculateBy(clientWarehouseDistances);
        }
        else if (isNullOrEmpty(clientWarehouseDistances)) {
            return calculateBy(supplierWarehouseDistances);
        }
        return calculateBy(supplierWarehouseDistances, clientWarehouseDistances);
    }

    private List<WarehouseAdviceDto> calculateBy(List<WarehouseToAssociateDistanceDto> associateDistances) {
        return sortedByTotalWeightedAvgDistance(getWeightedAvgDistancesGroupedByWarehouses(associateDistances));
    }

    private List<WarehouseAdviceDto> calculateBy(List<WarehouseToAssociateDistanceDto> suppliers,
                                                 List<WarehouseToAssociateDistanceDto> clients) {
        final List<WarehouseAdviceDto> advices = ListUtils.zipBy(
            getWeightedAvgDistancesGroupedByWarehouses(suppliers),
            getWeightedAvgDistancesGroupedByWarehouses(clients),
            (x, y) -> x.getWarehouse().getId().equals(y.getWarehouse().getId()),
            (x, y) -> new WarehouseAdviceDto(
                x.getWarehouse(),
                x.getTotalWeightedAvgDistance() + y.getTotalWeightedAvgDistance()
            )
        );
        return sortedByTotalWeightedAvgDistance(advices);
    }

    private List<WarehouseAdviceDto> sortedByTotalWeightedAvgDistance(List<WarehouseAdviceDto> warehouses) {
        return warehouses.stream()
            .sorted(Comparator.comparing(WarehouseAdviceDto::getTotalWeightedAvgDistance))
            .collect(Collectors.toUnmodifiableList());
    }

    private List<WarehouseAdviceDto> getWeightedAvgDistancesGroupedByWarehouses(List<WarehouseToAssociateDistanceDto> distances) {
        return distances.stream()
            .collect(Collectors.groupingBy(WarehouseToAssociateDistanceDto::getWarehouse,
                Collectors.averagingDouble(x -> x.getAssociate().getReverseWeight() * x.getDistance().inMeters)))
            .entrySet()
            .stream()
            .map(x -> new WarehouseAdviceDto(x.getKey(), x.getValue()))
            .collect(Collectors.toUnmodifiableList());
    }
}
