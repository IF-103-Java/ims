package com.ita.if103java.ims.service.impl;

import com.google.maps.model.DistanceMatrixElementStatus;
import com.ita.if103java.ims.dto.warehouse.advice.BestAssociatesDto;
import com.ita.if103java.ims.dto.warehouse.advice.BestAssociatesDto.WeightedBestAssociateDto;
import com.ita.if103java.ims.dto.warehouse.advice.TopWarehouseAddressDto;
import com.ita.if103java.ims.dto.warehouse.advice.WarehouseAdviceDto;
import com.ita.if103java.ims.dto.warehouse.advice.WarehouseItemAdviceDto;
import com.ita.if103java.ims.dto.warehouse.advice.WarehouseToAssociateDistanceDto;
import com.ita.if103java.ims.dto.warehouse.advice.WarehouseToAssociateDistancesDto;
import com.ita.if103java.ims.service.BestAssociatesService;
import com.ita.if103java.ims.service.TopWarehouseAddressService;
import com.ita.if103java.ims.service.WarehouseAdvisorCalculationService;
import com.ita.if103java.ims.service.WarehouseAdvisorService;
import com.ita.if103java.ims.service.WarehouseBestAssociateDistanceService;
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
        final List<TopWarehouseAddressDto> warehouseAddresses = warehouseAddressService.findAll(accountId);
        final BestAssociatesDto bestAssociates = bestAssociatesService.findByItem(accountId, itemId);
        final List<WeightedBestAssociateDto> associates = bestAssociates.getAssociates();
        final WarehouseToAssociateDistancesDto distances = distanceService.getDistances(warehouseAddresses, associates);
        final WarehouseToAssociateDistancesDto onlyAvailableRoutes = filterUnavailableRoute(distances);
        final List<WarehouseAdviceDto> advices = calculationService.calculate(onlyAvailableRoutes);
        return new WarehouseItemAdviceDto(itemId, advices, bestAssociates);
    }

    private WarehouseToAssociateDistancesDto filterUnavailableRoute(WarehouseToAssociateDistancesDto distances) {
        return new WarehouseToAssociateDistancesDto(
            keepIfAvailableRoute(distances.getSupplierWarehouseDistances()),
            keepIfAvailableRoute(distances.getClientWarehouseDistances())
        );
    }

    private List<WarehouseToAssociateDistanceDto> keepIfAvailableRoute(List<WarehouseToAssociateDistanceDto> distances) {
        return distances == null ? null : distances.stream()
            .filter(x -> x.getStatus() == DistanceMatrixElementStatus.OK)
            .collect(Collectors.toUnmodifiableList());
    }
}
