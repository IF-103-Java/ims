package com.ita.if103java.ims.service.impl;

import com.google.maps.model.DistanceMatrixElementStatus;
import com.ita.if103java.ims.dto.WarehouseAdviceDto;
import com.ita.if103java.ims.dto.WarehouseItemAdviceDto;
import com.ita.if103java.ims.dto.WarehouseToAssociateDistanceDto;
import com.ita.if103java.ims.dto.warehouse.advice.BestAssociatesDto;
import com.ita.if103java.ims.dto.warehouse.advice.TopWarehouseAddressDto;
import com.ita.if103java.ims.dto.warehouse.advice.WarehouseToAssociateDistancesDto;
import com.ita.if103java.ims.service.BestAssociatesService;
import com.ita.if103java.ims.service.TopWarehouseAddressService;
import com.ita.if103java.ims.service.WarehouseAdvisorCalculationService;
import com.ita.if103java.ims.service.WarehouseAdvisorService;
import com.ita.if103java.ims.service.WarehouseBestAssociateDistanceService;
import com.ita.if103java.ims.util.ListUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WarehouseAdvisorServiceImpl implements WarehouseAdvisorService {

    private final BestAssociatesService bestAssociatesService;
    private final TopWarehouseAddressService warehouseAddressService;
    private final WarehouseBestAssociateDistanceService distanceService;
    private final WarehouseAdvisorCalculationService calculationService;

    public WarehouseAdvisorServiceImpl(BestAssociatesService bestAssociatesService,
                                       TopWarehouseAddressService warehouseAddressService,
                                       WarehouseBestAssociateDistanceService distanceService,
                                       WarehouseAdvisorCalculationService calculationService) {
        this.bestAssociatesService = bestAssociatesService;
        this.warehouseAddressService = warehouseAddressService;
        this.distanceService = distanceService;
        this.calculationService = calculationService;
    }

    @Override
    public WarehouseItemAdviceDto getAdvice(Long accountId, Long itemId) {
        final BestAssociatesDto bestAssociates = bestAssociatesService.findByItem(accountId, itemId);
        final List<TopWarehouseAddressDto> warehouseAddresses = warehouseAddressService.findAll(accountId);
        final WarehouseToAssociateDistancesDto distances = distanceService.getDistances(
            warehouseAddresses,
            ListUtils.concat(bestAssociates.getSuppliers(), bestAssociates.getClients())
        );
        final List<WarehouseAdviceDto> advices = calculationService.calculate(filterUnavailableRoute(distances));
        return toDto(itemId, advices, bestAssociates);
    }

    private WarehouseItemAdviceDto toDto(Long itemId, List<WarehouseAdviceDto> advices, BestAssociatesDto bestAssociates) {
        return new WarehouseItemAdviceDto(itemId, advices, bestAssociates);
    }

    private WarehouseToAssociateDistancesDto filterUnavailableRoute(WarehouseToAssociateDistancesDto distances) {
        return new WarehouseToAssociateDistancesDto(
            keepIfAvailableRoute(distances.getSupplierWarehouseDistances()),
            keepIfAvailableRoute(distances.getClientWarehouseDistances())
        );
    }

    private List<WarehouseToAssociateDistanceDto> keepIfAvailableRoute(List<WarehouseToAssociateDistanceDto> distances) {
        return distances.stream()
            .filter(x -> x.getStatus() == DistanceMatrixElementStatus.OK)
            .collect(Collectors.toUnmodifiableList());
    }
}
