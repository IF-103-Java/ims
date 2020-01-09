package com.ita.if103java.ims.mapper.dto;

import com.google.common.collect.Lists;
import com.google.maps.model.DistanceMatrix;
import com.ita.if103java.ims.dto.AssociateAddressDto;
import com.ita.if103java.ims.dto.WarehouseAddressDto;
import com.ita.if103java.ims.dto.WarehouseToAssociateDistanceDto;
import com.ita.if103java.ims.util.ListUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component
public class DistanceMatrixDtoMapper {
    public List<WarehouseToAssociateDistanceDto> toDtoList(DistanceMatrix distanceMatrix,
                                                           List<WarehouseAddressDto> warehouseAddresses,
                                                           List<AssociateAddressDto> associateAddresses) {
        return ListUtils.zip(
            Lists.cartesianProduct(Arrays.asList(warehouseAddresses, associateAddresses)),
            Stream.of(distanceMatrix.rows).flatMap(x -> Stream.of(x.elements)).collect(Collectors.toList()),
            (x, y) -> new WarehouseToAssociateDistanceDto(
                (WarehouseAddressDto) x.get(0),
                (AssociateAddressDto) x.get(1),
                y.distance,
                y.status
            )
        );
    }
}
