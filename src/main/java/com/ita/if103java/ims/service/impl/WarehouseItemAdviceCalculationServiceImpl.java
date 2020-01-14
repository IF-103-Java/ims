package com.ita.if103java.ims.service.impl;

import com.google.maps.model.Distance;
import com.ita.if103java.ims.dto.WarehouseIdAdviceDto;
import com.ita.if103java.ims.dto.WarehouseToAssociateDistanceDto;
import com.ita.if103java.ims.dto.WeightAssociateDto;
import com.ita.if103java.ims.entity.AssociateType;
import com.ita.if103java.ims.service.WarehouseItemAdviceCalculationService;
import com.ita.if103java.ims.util.ListUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
public class WarehouseItemAdviceCalculationServiceImpl implements WarehouseItemAdviceCalculationService {

    @Override
    public List<WarehouseIdAdviceDto> calculate(List<WeightAssociateDto> bestSuppliers,
                                                List<WeightAssociateDto> bestClients,
                                                List<WarehouseToAssociateDistanceDto> warehouseAssociateDistances) {
        return sortedBySummarizedWeightedAvgDistance(bestSuppliers, bestClients, warehouseAssociateDistances);
    }

    private List<WarehouseIdAdviceDto> sortedBySummarizedWeightedAvgDistance(List<WeightAssociateDto> suppliers,
                                                                             List<WeightAssociateDto> clients,
                                                                             List<WarehouseToAssociateDistanceDto> warehouseAssociateDistances) {

        return ListUtils.zipBy(
            calculateWeightedAvgDistances(warehouseAssociateDistances, suppliers, AssociateType.SUPPLIER),
            calculateWeightedAvgDistances(warehouseAssociateDistances, clients, AssociateType.CLIENT),
            (x, y) -> x.getWarehouseId().equals(y.getWarehouseId()),
            (x, y) -> new WarehouseIdAdviceDto(
                x.getWarehouseId(),
                x.getWeightedAvgDistance() + y.getWeightedAvgDistance()
            )).stream()
            .sorted(Comparator.comparing(WarehouseIdAdviceDto::getWeightedAvgDistance))
            .collect(Collectors.toUnmodifiableList());
    }

    private List<WarehouseIdAdviceDto> calculateWeightedAvgDistances(List<WarehouseToAssociateDistanceDto> distances,
                                                                     List<WeightAssociateDto> associates,
                                                                     AssociateType associateType) {
        return getWeightedAvgDistancesGroupedByWarehouses(
            distances,
            associates,
            associateType,
            (associate, distance) -> distance.inMeters * associate.getReverseWeight()
        );
    }

    private List<WarehouseIdAdviceDto> getWeightedAvgDistancesGroupedByWarehouses(List<WarehouseToAssociateDistanceDto> distances,
                                                                                  List<WeightAssociateDto> associates,
                                                                                  AssociateType associateType,
                                                                                  BiFunction<WeightAssociateDto, Distance, Double> distanceFunction) {
        return distances.stream()
            .filter(x -> x.getAssociateAddress().getAssociateType() == associateType)
            .collect(Collectors.groupingBy(x -> x.getWarehouseAddress().getWarehouseId(),
                Collectors.averagingDouble(y -> distanceFunction.apply(
                    associates.stream()
                        .filter(a -> a.getAssociateId().equals(y.getAssociateAddress().getAssociateId()))
                        .findAny()
                        .orElseThrow(),
                    y.getDistance()))))
            .entrySet()
            .stream()
            .map(x -> new WarehouseIdAdviceDto(x.getKey(), x.getValue()))
            .collect(Collectors.toUnmodifiableList());
    }
}
