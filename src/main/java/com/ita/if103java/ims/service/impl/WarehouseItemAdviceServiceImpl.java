package com.ita.if103java.ims.service.impl;

import com.google.maps.model.DistanceMatrixElementStatus;
import com.ita.if103java.ims.dao.AddressLinkerDao;
import com.ita.if103java.ims.dao.TopWarehouseDao;
import com.ita.if103java.ims.dto.AssociateDto;
import com.ita.if103java.ims.dto.WarehouseAdviceDto;
import com.ita.if103java.ims.dto.WarehouseIdAdviceDto;
import com.ita.if103java.ims.dto.WarehouseItemAdviceDto;
import com.ita.if103java.ims.dto.WarehouseToAssociateDistanceDto;
import com.ita.if103java.ims.dto.WeightAssociateDto;
import com.ita.if103java.ims.service.AssociateService;
import com.ita.if103java.ims.service.BestTradeService;
import com.ita.if103java.ims.service.ItemService;
import com.ita.if103java.ims.service.WarehouseAssociateDistanceService;
import com.ita.if103java.ims.service.WarehouseItemAdviceCalculationService;
import com.ita.if103java.ims.service.WarehouseItemAdviceService;
import com.ita.if103java.ims.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WarehouseItemAdviceServiceImpl implements WarehouseItemAdviceService {
    private final WarehouseItemAdviceCalculationService calculationService;
    private final WarehouseAssociateDistanceService distanceService;
    private final BestTradeService bestTradeService;
    private final TopWarehouseDao topWarehouseDao;
    private final AddressLinkerDao addressLinkerDao;

    private final ItemService itemService;
    private final WarehouseService warehouseService;
    private final AssociateService associateService;

    @Autowired
    public WarehouseItemAdviceServiceImpl(WarehouseItemAdviceCalculationService calculationService,
                                          WarehouseAssociateDistanceService distanceService,
                                          BestTradeService bestTradeService,
                                          TopWarehouseDao topWarehouseDao,
                                          AddressLinkerDao addressLinkerDao,
                                          ItemService itemService,
                                          WarehouseService warehouseService,
                                          AssociateService associateService) {
        this.calculationService = calculationService;
        this.distanceService = distanceService;
        this.bestTradeService = bestTradeService;
        this.topWarehouseDao = topWarehouseDao;
        this.addressLinkerDao = addressLinkerDao;
        this.itemService = itemService;
        this.warehouseService = warehouseService;
        this.associateService = associateService;
    }

    @Override
    public WarehouseItemAdviceDto getAdvice(Long itemId, Long accountId) {
        final List<WeightAssociateDto> suppliers = bestTradeService.findBestSuppliersByItemId(itemId);
        final List<WeightAssociateDto> clients = bestTradeService.findBestClientsByItemId(itemId);
        final List<WarehouseToAssociateDistanceDto> warehouseAssociateDistances = keepIfAvailableRoute(
            distanceService.getDistances(
                addressLinkerDao.findWarehouseAddressesByIds(topWarehouseDao.findAllIds(accountId)),
                addressLinkerDao.findAssociateAddressesByIds(getAssociateIds(suppliers)),
                addressLinkerDao.findAssociateAddressesByIds(getAssociateIds(clients))
            )
        );
        return toDto(itemId, suppliers, clients, calculationService.calculate(suppliers, clients, warehouseAssociateDistances));
    }

    private WarehouseItemAdviceDto toDto(Long itemId,
                                         List<WeightAssociateDto> suppliers,
                                         List<WeightAssociateDto> clients,
                                         List<WarehouseIdAdviceDto> warehouseIdAdvices) {
        return new WarehouseItemAdviceDto(
            itemService.findById(itemId),
            mapWarehouseAdvices(warehouseIdAdvices),
            mapAssociates(suppliers),
            mapAssociates(clients)
        );
    }

    private List<AssociateDto> mapAssociates(List<WeightAssociateDto> associateDtoList) {
        return associateDtoList.stream()
            .map(WeightAssociateDto::getAssociateId)
            .map(associateService::view)
            .collect(Collectors.toUnmodifiableList());
    }

    private List<WarehouseAdviceDto> mapWarehouseAdvices(List<WarehouseIdAdviceDto> warehouseAdvices) {
        return warehouseAdvices.stream()
            .map(x -> new WarehouseAdviceDto(
                warehouseService.findWarehouseById(x.getWarehouseId()),
                x.getWeightedAvgDistance()
            ))
            .collect(Collectors.toUnmodifiableList());
    }

    private List<Long> getAssociateIds(List<WeightAssociateDto> suppliers) {
        return suppliers.stream()
            .map(WeightAssociateDto::getAssociateId)
            .collect(Collectors.toUnmodifiableList());
    }

    private List<WarehouseToAssociateDistanceDto> keepIfAvailableRoute(List<WarehouseToAssociateDistanceDto> distances) {
        return distances.stream()
            .filter(x -> x.getStatus() == DistanceMatrixElementStatus.OK)
            .collect(Collectors.toUnmodifiableList());
    }
}
