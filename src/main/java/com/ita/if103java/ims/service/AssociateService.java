package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.AssociateDto;
import com.ita.if103java.ims.dto.SavedItemAssociateDto;
import com.ita.if103java.ims.entity.AssociateType;
import com.ita.if103java.ims.security.UserDetailsImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AssociateService {
    AssociateDto create(UserDetailsImpl user, AssociateDto associateDto);

    AssociateDto update(UserDetailsImpl user, AssociateDto associateDto);

    List<AssociateDto> findSortedAssociates(Pageable pageable, UserDetailsImpl user);

    AssociateDto view(UserDetailsImpl user, Long id);

    boolean delete(UserDetailsImpl user, Long id);

    List<SavedItemAssociateDto> getAssociatesByNameAndType(UserDetailsImpl user, String name, AssociateType type);
}
