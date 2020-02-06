package com.ita.if103java.ims.mapper.dto;

import com.ita.if103java.ims.dto.SavedItemAssociateDto;
import com.ita.if103java.ims.entity.Associate;
import org.springframework.stereotype.Component;

@Component
public class SavedItemAssociateDtoMapper {
   public SavedItemAssociateDto toDto(Associate associate){
        if (associate == null){
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
