package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.PopularItemsRequestDto;
import com.ita.if103java.ims.dto.PopularItemsDto;
import com.ita.if103java.ims.dto.EndingItemsDto;
import com.ita.if103java.ims.dto.WarehouseLoadDto;

import java.util.List;

public interface DashboardService {
    List<WarehouseLoadDto> getWarehouseLoad();
    List<PopularItemsDto> getPopularItems(PopularItemsRequestDto popularItems);
    List<EndingItemsDto> getEndingItems(int minQuantity);
}
