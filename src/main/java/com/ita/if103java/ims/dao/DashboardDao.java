package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.dto.EndingItemsDto;
import com.ita.if103java.ims.dto.PopularItemsDto;
import com.ita.if103java.ims.dto.PopularItemsRequestDto;
import com.ita.if103java.ims.dto.WarehouseLoadDto;
import com.ita.if103java.ims.dto.WarehousePremiumStructDto;

import java.util.List;

public interface DashboardDao {
    List<WarehouseLoadDto> findWarehouseLoadByAccountId(Long accountId);

    List<PopularItemsDto> findPopularItems(PopularItemsRequestDto popularItems);

    List<EndingItemsDto> findEndedItemsByAccountId(int minQuantity, Long accountId);

    WarehousePremiumStructDto getPreLoadByAccounId(Long id, Long accountId);
}
