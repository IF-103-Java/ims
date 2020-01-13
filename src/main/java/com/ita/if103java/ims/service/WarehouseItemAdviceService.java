package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.WarehouseItemAdviceDto;
import com.ita.if103java.ims.security.UserDetailsImpl;

public interface WarehouseItemAdviceService {
    WarehouseItemAdviceDto getAdvice(Long itemId, UserDetailsImpl userDetails);
}
