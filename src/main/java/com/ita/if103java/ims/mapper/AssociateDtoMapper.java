package com.ita.if103java.ims.mapper;

import com.ita.if103java.ims.dto.AssociateDto;
import com.ita.if103java.ims.entity.Associate;
import org.springframework.stereotype.Component;

@Component
public class AssociateDtoMapper {

    public AssociateDto convertAssociateToAssociateDto(Associate associate) {
        if (associate == null) {
            return null;
        } else {
            AssociateDto associateDto = new AssociateDto();
            associateDto.setId(associate.getId());
            associateDto.setAccountId(associate.getAccountId());
            associateDto.setName(associate.getName());
            associateDto.setEmail(associate.getEmail());
            associateDto.setPhone(associate.getPhone());
            associateDto.setAdditionalInfo(associate.getAdditionalInfo());
            associateDto.setType(associate.getType());

            return associateDto;
        }
    }

    public Associate convertAssociateDtoToAssociate(AssociateDto associateDto) {
        if (associateDto == null) {
            return null;
        } else {
            Associate associate = new Associate();
            associate.setId(associateDto.getId());
            associate.setAccountId(associateDto.getAccountId());
            associate.setName(associateDto.getName());
            associate.setEmail(associateDto.getEmail());
            associate.setPhone(associateDto.getPhone());
            associate.setAdditionalInfo(associateDto.getAdditionalInfo());
            associate.setType(associateDto.getType());

            return associate;
        }
    }
}
