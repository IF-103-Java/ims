package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.AssociateDto;
import com.ita.if103java.ims.security.UserDetailsImpl;

import java.util.List;
import java.util.Optional;

public interface AssociateService {
    Optional<AssociateDto> create(UserDetailsImpl user, AssociateDto associateDto);

    AssociateDto update(UserDetailsImpl user, AssociateDto associateDto);

    AssociateDto view(UserDetailsImpl user, Long id);

    List<AssociateDto> findAll();

    boolean delete(UserDetailsImpl user, Long id);
}
