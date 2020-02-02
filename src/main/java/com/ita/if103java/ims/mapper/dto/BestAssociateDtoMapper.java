package com.ita.if103java.ims.mapper.dto;

import com.ita.if103java.ims.dto.warehouse.advice.Address;
import com.ita.if103java.ims.dto.warehouse.advice.Address.Geo;
import com.ita.if103java.ims.dto.warehouse.advice.BestAssociateDto;
import com.ita.if103java.ims.dto.warehouse.advice.BestAssociateDto.Associate;
import com.ita.if103java.ims.entity.AssociateAddressTotalTransactionQuantity;
import org.springframework.stereotype.Component;

@Component
public class BestAssociateDtoMapper extends AbstractEntityDtoMapper<AssociateAddressTotalTransactionQuantity, BestAssociateDto> {

    @Override
    public AssociateAddressTotalTransactionQuantity toEntity(BestAssociateDto dto) {
        return dto == null ? null : new AssociateAddressTotalTransactionQuantity(
            dto.getReference().getId(),
            dto.getReference().getName(),
            dto.getReference().getType(),
            dto.getReference().getAddress().getCountry(),
            dto.getReference().getAddress().getCity(),
            dto.getReference().getAddress().getStreet(),
            dto.getReference().getAddress().getGeo().getLatitude(),
            dto.getReference().getAddress().getGeo().getLongitude(),
            dto.getTotalTransactionQuantity()
        );
    }

    @Override
    public BestAssociateDto toDto(AssociateAddressTotalTransactionQuantity entity) {
        return entity == null ? null : new BestAssociateDto(
            new Associate(
                entity.getAssociateId(),
                entity.getAssociateName(),
                new Address(
                    entity.getCountry(),
                    entity.getCity(),
                    entity.getAddress(),
                    new Geo(entity.getLatitude(), entity.getLongitude())
                ),
                entity.getAssociateType()
            ),
            entity.getTotalTransactionQuantity()
        );
    }
}
