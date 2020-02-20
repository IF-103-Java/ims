package com.ita.if103java.ims.mapper.dto;

import com.ita.if103java.ims.dto.AssociateDto;
import com.ita.if103java.ims.entity.Associate;
import org.springframework.stereotype.Component;

@Component
public class AssociateDtoMapper extends AbstractEntityDtoMapper<Associate, AssociateDto> {

    @Override
    public Associate toEntity(AssociateDto dto) {
        if (dto == null) {
            return null;
        } else {
            Associate associate = new Associate();
            associate.setId(dto.getId());
            associate.setAccountId(dto.getAccountId());
            associate.setName(dto.getName());
            associate.setEmail(dto.getEmail());
            associate.setPhone(dto.getPhone());
            associate.setAdditionalInfo(dto.getAdditionalInfo());
            associate.setType(dto.getType());
            associate.setActive(dto.isActive());

            return associate;
        }
    }

    @Override
    public AssociateDto toDto(Associate entity) {
        if (entity == null) {
            return null;
        } else {
            AssociateDto associateDto = new AssociateDto();
            associateDto.setId(entity.getId());
            associateDto.setAccountId(entity.getAccountId());
            associateDto.setName(entity.getName());
            associateDto.setEmail(entity.getEmail());
            associateDto.setPhone(entity.getPhone());
            associateDto.setAdditionalInfo(entity.getAdditionalInfo());
            associateDto.setType(entity.getType());
            associateDto.setActive(entity.isActive());

            return associateDto;
        }
    }
}
