package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.warehouse.advice.associate.BestWeightedAssociatesDto;

public interface BestAssociatesService {
    BestWeightedAssociatesDto findByItem(Long accountId, Long itemId);
}
