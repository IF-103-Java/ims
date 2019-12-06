package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.DashboardDao;
import com.ita.if103java.ims.dto.PopularityListDto;
import com.ita.if103java.ims.dto.RefillListDto;
import com.ita.if103java.ims.dto.WarehouseLoadDto;
import com.ita.if103java.ims.entity.DateType;
import com.ita.if103java.ims.entity.PopType;
import com.ita.if103java.ims.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {
    private DashboardDao dashboardDao;

    @Autowired
    public DashboardServiceImpl(DashboardDao dashboardDao) {
        this.dashboardDao = dashboardDao;
    }

    @Override
    public List<WarehouseLoadDto> getWarehouseWidget() {
        return dashboardDao.findWarehouseWidgets();
    }

    @Override
    public List<PopularityListDto> getPopularityList(int quantity, DateType dateType, PopType popType, String date) {
        return dashboardDao.findPopularityItems(quantity, dateType, popType, date);
    }


    @Override
    public List<RefillListDto> getRefillList(int minQuantity) {
        return dashboardDao.findEndedItems(minQuantity);
    }
}
