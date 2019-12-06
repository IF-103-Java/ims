package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.dto.PopularityListDto;
import com.ita.if103java.ims.dto.RefillListDto;
import com.ita.if103java.ims.dto.WarehouseLoadDto;
import com.ita.if103java.ims.dto.WarehousePremiumStructDto;
import java.util.List;

public interface DashboardDao {
    List<WarehouseLoadDto> findWarehouseWidgets();
    List<PopularityListDto> findPopularItems(int quantity, String type, String date);
    List<PopularityListDto> findUnpopularItems(int quantity, String type, String date);
    List<RefillListDto> findEndedItems(int minQuantity);
    List<WarehousePremiumStructDto> getPreStructure(Long id);
    List<WarehouseLoadDto> getPreLoad(Long id);
}
