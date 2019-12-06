package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.dto.PopularityListDto;
import com.ita.if103java.ims.dto.RefillListDto;
import com.ita.if103java.ims.dto.WarehouseLoadDto;
import com.ita.if103java.ims.dto.WarehousePremiumStructDto;
import com.ita.if103java.ims.entity.DateType;
import com.ita.if103java.ims.entity.PopType;

import java.util.List;

public interface DashboardDao {
    List<WarehouseLoadDto> findWarehouseWidgets();
    List<PopularityListDto> findPopularityItems(int quantity, DateType dateType, PopType popType, String date);
    List<RefillListDto> findEndedItems(int minQuantity);
    List<WarehousePremiumStructDto> getPreStructure(Long id);
    List<WarehouseLoadDto> getPreLoad(Long id);
}
