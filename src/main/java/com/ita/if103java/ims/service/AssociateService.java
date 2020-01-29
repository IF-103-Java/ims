package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.AssociateDto;
import com.ita.if103java.ims.security.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AssociateService {
    AssociateDto create(UserDetailsImpl user, AssociateDto associateDto);

    AssociateDto update(UserDetailsImpl user, AssociateDto associateDto);

    Page<AssociateDto> findSortedAssociates(Pageable pageable, UserDetailsImpl user);

    AssociateDto view(UserDetailsImpl user, Long id);

    boolean delete(UserDetailsImpl user, Long id);
}
