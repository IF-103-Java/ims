package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.AssociateDto;

import java.util.Optional;

public interface AssociateService {
    Optional<AssociateDto> create(AssociateDto associateDto);

    AssociateDto update(AssociateDto associateDto);

    AssociateDto view(Long id);

    boolean delete(Long id);
}
