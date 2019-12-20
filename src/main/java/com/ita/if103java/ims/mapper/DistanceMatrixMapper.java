package com.ita.if103java.ims.mapper;

import com.google.common.collect.Lists;
import com.google.maps.model.DistanceMatrix;
import com.ita.if103java.ims.dto.AddressDto;
import com.ita.if103java.ims.dto.WarehouseToAssociateDistanceDto;
import com.ita.if103java.ims.util.ListUtil;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component
public class DistanceMatrixMapper {
    public List<WarehouseToAssociateDistanceDto> toDtoList(DistanceMatrix distanceMatrix,
                                                           List<AddressDto> warehouseAddresses,
                                                           List<AddressDto> associateAddresses) {
        return ListUtil.zip(
            Lists.cartesianProduct(Arrays.asList(warehouseAddresses, associateAddresses)),
            Stream.of(distanceMatrix.rows).flatMap(x -> Stream.of(x.elements)).collect(Collectors.toList()),
            (x, y) -> new WarehouseToAssociateDistanceDto(x.get(0), x.get(1), y.distance)
        );
    }
}
