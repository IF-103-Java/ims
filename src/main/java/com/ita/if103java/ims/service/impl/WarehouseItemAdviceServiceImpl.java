package com.ita.if103java.ims.service.impl;

import com.google.maps.model.DistanceMatrixElementStatus;
import com.ita.if103java.ims.dao.AddressLinkerDao;
import com.ita.if103java.ims.dao.TopWarehouseDao;
import com.ita.if103java.ims.dto.AssociateDto;
import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.dto.WarehouseAdviceDto;
import com.ita.if103java.ims.dto.WarehouseIdAdviceDto;
import com.ita.if103java.ims.dto.WarehouseItemAdviceDto;
import com.ita.if103java.ims.dto.WarehouseToAssociateDistanceDto;
import com.ita.if103java.ims.dto.WeightAssociateDto;
import com.ita.if103java.ims.exception.service.ImpossibleWarehouseAdviceException;
import com.ita.if103java.ims.security.UserDetailsImpl;
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
    public WarehouseItemAdviceDto getAdvice(Long itemId, UserDetailsImpl userDetails) {
        final ItemDto item = itemService.findById(itemId, userDetails);

        final List<WeightAssociateDto> suppliers = bestTradeService.findBestSuppliersByItemId(itemId);
        final List<WeightAssociateDto> clients = bestTradeService.findBestClientsByItemId(itemId);
        final List<Long> supplierIds = getAssociateIds(suppliers);
        final List<Long> clientIds = getAssociateIds(clients);
        final List<Long> topWarehouseIds = topWarehouseDao.findAllIds(userDetails.getUser().getAccountId());

        if (supplierIds.isEmpty() || clientIds.isEmpty() || topWarehouseIds.isEmpty()) {
            throw new ImpossibleWarehouseAdviceException("Your account doesn't have enough valuable info to provide an advice");
        }

        final List<WarehouseToAssociateDistanceDto> distances = keepIfAvailableRoute(
            distanceService.getDistances(
                addressLinkerDao.findWarehouseAddressesByIds(topWarehouseIds),
                addressLinkerDao.findAssociateAddressesByIds(supplierIds),
                addressLinkerDao.findAssociateAddressesByIds(clientIds)
            )
        );
        return toDto(item, userDetails, suppliers, clients, calculationService.calculate(suppliers, clients, distances));
    }

    private WarehouseItemAdviceDto toDto(ItemDto item,
                                         UserDetailsImpl userDetails,
                                         List<WeightAssociateDto> suppliers,
                                         List<WeightAssociateDto> clients,
                                         List<WarehouseIdAdviceDto> warehouseIdAdvices) {
        return new WarehouseItemAdviceDto(
            item,
            mapWarehouseAdvices(warehouseIdAdvices, userDetails),
            mapAssociates(suppliers, userDetails),
            mapAssociates(clients, userDetails)
        );
    }

    private List<AssociateDto> mapAssociates(List<WeightAssociateDto> associateDtoList, UserDetailsImpl userDetails) {
        return associateDtoList.stream()
            .map(WeightAssociateDto::getAssociateId)
            .map(id -> associateService.view(userDetails, id))
            .collect(Collectors.toUnmodifiableList());
    }

    private List<WarehouseAdviceDto> mapWarehouseAdvices(List<WarehouseIdAdviceDto> warehouseAdvices, UserDetailsImpl userDetails) {
        return warehouseAdvices.stream()
            .map(x -> new WarehouseAdviceDto(
                warehouseService.findById(x.getWarehouseId(), userDetails),
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
