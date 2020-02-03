package com.ita.if103java.ims.service.impl;

import com.google.maps.model.DistanceMatrixElementStatus;
import com.ita.if103java.ims.dto.warehouse.advice.TopWarehouseAddressDto;
import com.ita.if103java.ims.dto.warehouse.advice.advice.AdviceDataProvider;
import com.ita.if103java.ims.dto.warehouse.advice.advice.WarehouseAdviceDto;
import com.ita.if103java.ims.dto.warehouse.advice.advice.WarehouseItemAdviceDto;
import com.ita.if103java.ims.dto.warehouse.advice.associate.BestWeightedAssociateDto;
import com.ita.if103java.ims.dto.warehouse.advice.associate.BestWeightedAssociatesDto;
import com.ita.if103java.ims.dto.warehouse.advice.distance.WarehouseToAssociateDistanceDto;
import com.ita.if103java.ims.dto.warehouse.advice.distance.WarehouseToAssociateDistancesDto;
import com.ita.if103java.ims.exception.service.AsyncDbFetchException;
import com.ita.if103java.ims.service.BestAssociatesService;
import com.ita.if103java.ims.service.TopWarehouseAddressService;
import com.ita.if103java.ims.service.WarehouseAdvisorCalculationService;
import com.ita.if103java.ims.service.WarehouseAdvisorService;
import com.ita.if103java.ims.service.WarehouseBestAssociateDistanceService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class WarehouseAdvisorServiceImpl implements WarehouseAdvisorService {

    private final BestAssociatesService bestAssociatesService;
    private final TopWarehouseAddressService warehouseAddressService;
    private final WarehouseBestAssociateDistanceService distanceService;
    private final WarehouseAdvisorCalculationService calculationService;
    private final ExecutorService executorService;

    public WarehouseAdvisorServiceImpl(BestAssociatesService bestAssociatesService,
                                       TopWarehouseAddressService warehouseAddressService,
                                       WarehouseBestAssociateDistanceService distanceService,
                                       WarehouseAdvisorCalculationService calculationService,
                                       @Qualifier("dbFixedThreadPool") ExecutorService executorService) {
        this.bestAssociatesService = bestAssociatesService;
        this.warehouseAddressService = warehouseAddressService;
        this.distanceService = distanceService;
        this.calculationService = calculationService;
        this.executorService = executorService;
    }

    @Override
    public WarehouseItemAdviceDto getAdvice(Long accountId, Long itemId) {
        final AdviceDataProvider adviceDataProvider = fetchAsync(
            () -> warehouseAddressService.findAll(accountId),
            () -> bestAssociatesService.findByItem(accountId, itemId),
            executorService
        );
        final List<TopWarehouseAddressDto> warehouseAddresses = adviceDataProvider.getWarehouseAddresses();
        final BestWeightedAssociatesDto bestAssociates = adviceDataProvider.getBestAssociates();
        final List<BestWeightedAssociateDto> associates = bestAssociates.getAssociates();
        final WarehouseToAssociateDistancesDto distances = distanceService.getDistances(warehouseAddresses, associates);
        final WarehouseToAssociateDistancesDto onlyAvailableRoutes = filterUnavailableRoute(distances);
        final List<WarehouseAdviceDto> advices = calculationService.calculate(onlyAvailableRoutes);
        return new WarehouseItemAdviceDto(itemId, advices, bestAssociates);
    }

    private AdviceDataProvider fetchAsync(Supplier<List<TopWarehouseAddressDto>> topWarehousesSupplier,
                                          Supplier<BestWeightedAssociatesDto> associatesSupplier,
                                          Executor executor) {
        final CompletableFuture<AdviceDataProvider> future = CompletableFuture.supplyAsync(topWarehousesSupplier, executor)
            .thenCombine(CompletableFuture.supplyAsync(associatesSupplier, executor), AdviceDataProvider::new);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new AsyncDbFetchException("Async fetch error during db call", e);
        }
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
