package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.AssociateDto;

import java.util.List;
import java.util.Optional;

public interface AssociateService {
    Optional<AssociateDto> create(AssociateDto associateDto);

    AssociateDto update(AssociateDto associateDto);

    AssociateDto view(Long id);

    List<AssociateDto> findAll();

    boolean delete(Long id);
}
