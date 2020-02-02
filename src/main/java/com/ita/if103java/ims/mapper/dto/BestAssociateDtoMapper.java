package com.ita.if103java.ims.mapper.dto;

import com.ita.if103java.ims.dto.BestAssociateDto;
import com.ita.if103java.ims.dto.BestAssociateDto.Associate;
import com.ita.if103java.ims.dto.BestAssociateDto.Associate.Address;
import com.ita.if103java.ims.dto.BestAssociateDto.Associate.Address.Geo;
import com.ita.if103java.ims.entity.AssociateAddressTotalTransactionQuantity;
import org.springframework.stereotype.Component;

@Component
public class BestAssociateDtoMapper extends AbstractEntityDtoMapper<AssociateAddressTotalTransactionQuantity, BestAssociateDto> {

    @Override
    public AssociateAddressTotalTransactionQuantity toEntity(BestAssociateDto dto) {
        return dto == null ? null : new AssociateAddressTotalTransactionQuantity(
            dto.getAssociate().getId(),
            dto.getAssociate().getName(),
            dto.getAssociate().getType(),
            dto.getAssociate().getAddress().getCountry(),
            dto.getAssociate().getAddress().getCity(),
            dto.getAssociate().getAddress().getStreet(),
            dto.getAssociate().getAddress().getGeo().getLatitude(),
            dto.getAssociate().getAddress().getGeo().getLongitude(),
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
