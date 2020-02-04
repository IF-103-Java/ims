package com.ita.if103java.ims.mapper.dto;

import com.ita.if103java.ims.dto.warehouse.advice.Address;
import com.ita.if103java.ims.dto.warehouse.advice.Address.Geo;
import com.ita.if103java.ims.dto.warehouse.advice.TopWarehouseAddressDto;
import com.ita.if103java.ims.entity.TopWarehouseAddress;
import org.springframework.stereotype.Component;

@Component
public class TopWarehouseAddressDtoMapper extends AbstractEntityDtoMapper<TopWarehouseAddress, TopWarehouseAddressDto> {

    @Override
    public TopWarehouseAddress toEntity(TopWarehouseAddressDto dto) {
        return dto == null ? null : new TopWarehouseAddress(
            dto.getId(),
            dto.getName(),
            dto.getAddress().getCountry(),
            dto.getAddress().getCity(),
            dto.getAddress().getStreet(),
            dto.getAddress().getGeo().getLatitude(),
            dto.getAddress().getGeo().getLongitude()
        );
    }

    @Override
    public TopWarehouseAddressDto toDto(TopWarehouseAddress entity) {
        return entity == null ? null : new TopWarehouseAddressDto(
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
