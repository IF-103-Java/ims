package com.ita.if103java.ims.mapper.dto;

import com.ita.if103java.ims.dto.warehouse.advice.Address;
import com.ita.if103java.ims.dto.warehouse.advice.Address.Geo;
import com.ita.if103java.ims.dto.warehouse.advice.associate.BestAssociateDto;
import com.ita.if103java.ims.dto.warehouse.advice.associate.Associate;
import com.ita.if103java.ims.entity.AssociateAddressTotalTransactionQuantity;
import org.springframework.stereotype.Component;

@Component
public class BestAssociateDtoMapper extends AbstractEntityDtoMapper<AssociateAddressTotalTransactionQuantity, BestAssociateDto> {

    @Override
    public AssociateAddressTotalTransactionQuantity toEntity(BestAssociateDto dto) {
        return dto == null ? null : new AssociateAddressTotalTransactionQuantity(
            dto.getId(),
            dto.getName(),
            dto.getType(),
            dto.getAddress().getCountry(),
            dto.getAddress().getCity(),
            dto.getAddress().getStreet(),
            dto.getAddress().getGeo().getLatitude(),
            dto.getAddress().getGeo().getLongitude(),
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
