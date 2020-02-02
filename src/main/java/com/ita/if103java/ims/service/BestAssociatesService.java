package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.BestAssociatesDto;

public interface BestAssociatesService {
    BestAssociatesDto findByItem(Long accountId, Long itemId);
}
