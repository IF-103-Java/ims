package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.PopularityListDto;
import com.ita.if103java.ims.dto.RefillListDto;
import com.ita.if103java.ims.dto.WarehouseLoadDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DashboardService {
    List<WarehouseLoadDto> getWarehouseWidget();
    List<PopularityListDto> getPopularList(int quantity, String type, String date);
    List<PopularityListDto> getUnpopularList(int quantity, String type, String date);
    List<RefillListDto> getRefillList(int minQuantity);
}
