package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.warehouse.advice.BestAssociatesDto;

public interface BestAssociatesService {
    BestAssociatesDto findByItem(Long accountId, Long itemId);
}
