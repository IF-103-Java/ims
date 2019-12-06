package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.PopularityListDto;
import com.ita.if103java.ims.dto.RefillListDto;
import com.ita.if103java.ims.dto.WarehouseLoadDto;
import com.ita.if103java.ims.entity.DateType;
import com.ita.if103java.ims.entity.PopType;

import java.util.List;

public interface DashboardService {
    List<WarehouseLoadDto> getWarehouseWidget();
    List<PopularityListDto> getPopularityList(int quantity, DateType dateType, PopType popType, String date);
    List<RefillListDto> getRefillList(int minQuantity);
}
