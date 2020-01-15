package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.AssociateDto;
import com.ita.if103java.ims.security.UserDetailsImpl;

import java.util.List;

public interface AssociateService {
    AssociateDto create(UserDetailsImpl user, AssociateDto associateDto);

    AssociateDto update(UserDetailsImpl user, AssociateDto associateDto);

    AssociateDto view(UserDetailsImpl user, Long id);

    List<AssociateDto> findByAccountId(Long accountId);

    boolean delete(UserDetailsImpl user, Long id);
}
