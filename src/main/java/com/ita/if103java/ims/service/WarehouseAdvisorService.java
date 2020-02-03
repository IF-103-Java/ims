package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.warehouse.advice.advice.WarehouseItemAdviceDto;

public interface WarehouseAdvisorService {
    WarehouseItemAdviceDto getAdvice(Long accountId, Long itemId);
}
