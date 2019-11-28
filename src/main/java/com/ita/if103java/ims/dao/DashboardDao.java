package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.dto.PopularityListDto;
import com.ita.if103java.ims.dto.RefillListDto;
import com.ita.if103java.ims.dto.WarehouseLoadDto;

import java.util.List;

public interface DashboardDao {
    List<WarehouseLoadDto> findWarehouseWidgets();
    List<PopularityListDto> findPopularItems();
    List<PopularityListDto> findUnpopularItems();
    List<RefillListDto> findEndedItems();
}
