package com.ita.if103java.ims.mapper.dto;

import com.google.common.collect.Lists;
import com.google.maps.model.DistanceMatrix;
import com.ita.if103java.ims.dto.WarehouseToAssociateDistanceDto;
import com.ita.if103java.ims.dto.warehouse.advice.BestAssociatesDto.WeightedBestAssociateDto;
import com.ita.if103java.ims.dto.warehouse.advice.TopWarehouseAddressDto;
import com.ita.if103java.ims.dto.warehouse.advice.WarehouseToAssociateDistancesDto;
import com.ita.if103java.ims.entity.AssociateType;
import com.ita.if103java.ims.util.ListUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component
public class WarehouseAdvisorDistanceMatrixDtoMapper {
    public WarehouseToAssociateDistancesDto toDtoList(DistanceMatrix distanceMatrix,
                                                      List<TopWarehouseAddressDto> warehouseAddresses,
                                                      List<WeightedBestAssociateDto> associateAddresses) {
        final Map<AssociateType, List<WarehouseToAssociateDistanceDto>> distances = ListUtils.zip(
            Lists.cartesianProduct(Arrays.asList(warehouseAddresses, associateAddresses)),
            Stream.of(distanceMatrix.rows).flatMap(x -> Stream.of(x.elements)).collect(Collectors.toList()),
            (x, y) -> new WarehouseToAssociateDistanceDto(
                (TopWarehouseAddressDto) x.get(0),
                (WeightedBestAssociateDto) x.get(1),
                y.distance,
                y.status
            )
        ).stream().collect(Collectors.groupingBy(x -> x.getAssociate().getReference().getReference().getType()));
        return new WarehouseToAssociateDistancesDto(
            distances.get(AssociateType.SUPPLIER),
            distances.get(AssociateType.CLIENT)
        );
    }
}
