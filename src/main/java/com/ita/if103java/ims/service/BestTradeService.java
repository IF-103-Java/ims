package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.WeightAssociateDto;

import java.util.List;

public interface BestTradeService {
    List<WeightAssociateDto> findBestSuppliersByItemId(Long itemId);

    List<WeightAssociateDto> findBestClientsByItemId(Long itemId);
}
