package com.ita.if103java.ims.mapper.dto;

import com.ita.if103java.ims.dto.warehouse.advice.Address;
import com.ita.if103java.ims.dto.warehouse.advice.Address.Geo;
import com.ita.if103java.ims.dto.warehouse.advice.TopWarehouseDto;
import com.ita.if103java.ims.entity.TopWarehouseAddress;
import org.springframework.stereotype.Component;

@Component
public class TopWarehouseAddressDtoMapper extends AbstractEntityDtoMapper<TopWarehouseAddress, TopWarehouseDto> {

    @Override
    public TopWarehouseAddress toEntity(TopWarehouseDto dto) {
        return dto == null ? null : new TopWarehouseAddress(
            dto.getWarehouseId(),
            dto.getWarehouseName(),
            dto.getAddress().getCountry(),
            dto.getAddress().getCity(),
            dto.getAddress().getStreet(),
            dto.getAddress().getGeo().getLatitude(),
            dto.getAddress().getGeo().getLongitude()
        );
    }

    @Override
    public TopWarehouseDto toDto(TopWarehouseAddress entity) {
        return entity == null ? null : new TopWarehouseDto(
            entity.getWarehouseId(),
            entity.getWarehouseName(),
            new Address(
                entity.getCountry(),
                entity.getCity(),
                entity.getAddress(),
                new Geo(entity.getLatitude(), entity.getLongitude())
            )
        );
    }

}
