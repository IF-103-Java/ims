package com.ita.if103java.ims.service.impl;

import com.google.maps.model.DistanceMatrixElementStatus;
import com.ita.if103java.ims.dto.WarehouseItemAdviceDto;
import com.ita.if103java.ims.dto.WarehouseToAssociateDistanceDto;
import com.ita.if103java.ims.dto.warehouse.advice.BestAssociatesDto;
import com.ita.if103java.ims.dto.warehouse.advice.WarehouseToAssociateDistancesDto;
import com.ita.if103java.ims.exception.service.ImpossibleWarehouseAdviceException;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.BestAssociatesService;
import com.ita.if103java.ims.service.ItemService;
import com.ita.if103java.ims.service.WarehouseAdvisorCalculationService;
import com.ita.if103java.ims.service.WarehouseAdvisorService;
import com.ita.if103java.ims.service.WarehouseAssociateDistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WarehouseAdvisorServiceImpl implements WarehouseAdvisorService {
    private final WarehouseAdvisorCalculationService calculationService;
    private final WarehouseAssociateDistanceService distanceService;
    private final BestAssociatesService bestAssociatesService;

    @Autowired
    public WarehouseAdvisorServiceImpl(WarehouseAdvisorCalculationService calculationService,
                                       WarehouseAssociateDistanceService distanceService,
                                       BestAssociatesService bestAssociatesService, ItemService itemService) {
        this.calculationService = calculationService;
        this.distanceService = distanceService;
        this.bestAssociatesService = bestAssociatesService;
    }

    @Override
    public WarehouseItemAdviceDto getAdvice(Long itemId, UserDetailsImpl userDetails) {
        final BestAssociatesDto bestAssociates = bestAssociatesService.findByItem(userDetails.getUser().getAccountId(), itemId);
        if (bestAssociates.getClients().isEmpty() || bestAssociates.getSuppliers().isEmpty()) {
            throw new ImpossibleWarehouseAdviceException("Your account doesn't have enough valuable info to provide an advice");
        }
        final WarehouseToAssociateDistancesDto distances = distanceService.getDistances(null, null);

//        final List<WarehouseToAssociateDistanceDto> distances = keepIfAvailableRoute(
//            distanceService.getDistances(
//                addressLinkerDao.findWarehouseAddressesByIds(topWarehouseIds),
//                addressLinkerDao.findAssociateAddressesByIds(supplierIds),
////                addressLinkerDao.findAssociateAddressesByIds(clientIds)
//            )
//        );
//        return toDto(item, userDetails, suppliers, clients, calculationService.calculate(suppliers, clients, distances));
        return toDto();
    }

    private WarehouseItemAdviceDto toDto() {
        return new WarehouseItemAdviceDto();
    }

    private List<WarehouseToAssociateDistanceDto> keepIfAvailableRoute(List<WarehouseToAssociateDistanceDto> distances) {
        return distances.stream()
            .filter(x -> x.getStatus() == DistanceMatrixElementStatus.OK)
            .collect(Collectors.toUnmodifiableList());
    }
}
