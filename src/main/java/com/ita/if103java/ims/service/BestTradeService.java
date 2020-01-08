package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.BestTradeDto;

import java.util.List;

public interface BestTradeService {
    List<BestTradeDto> findBestSuppliersByItemId(Long itemId);

    List<BestTradeDto> findBestClientsByItemId(Long itemId);
}
