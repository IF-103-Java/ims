package com.ita.if103java.ims.mapper.dto;

import com.ita.if103java.ims.dto.SavedItemAssociateDto;
import com.ita.if103java.ims.entity.Associate;
import org.springframework.stereotype.Component;

@Component
public class SavedItemAssociateDtoMapper extends AbstractEntityDtoMapper<Associate, SavedItemAssociateDto> {

    @Override
    public Associate toEntity(SavedItemAssociateDto dto) {
        if (dto == null) {
            return null;
        }
        Associate associate = new Associate();
        associate.setId(dto.getId());
        associate.setName(associate.getName());
        associate.setEmail(associate.getEmail());
        associate.setPhone(associate.getPhone());
        return null;
    }

    @Override
    public SavedItemAssociateDto toDto(Associate associate) {
        if (associate == null) {
            return null;
        }
        SavedItemAssociateDto savedItemAssociateDto = new SavedItemAssociateDto();
        savedItemAssociateDto.setId(associate.getId());
        savedItemAssociateDto.setName(associate.getName());
        savedItemAssociateDto.setEmail(associate.getEmail());
        savedItemAssociateDto.setPhone(associate.getPhone());
        return savedItemAssociateDto;
    }
}
