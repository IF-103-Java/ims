package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.WarehouseItemAdviceDto;

public interface WarehouseItemAdviceService {
    WarehouseItemAdviceDto getAdvice(Long itemId, Long accountId);
}
