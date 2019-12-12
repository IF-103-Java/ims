package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.DashboardDao;
import com.ita.if103java.ims.dto.EndingItemsDto;
import com.ita.if103java.ims.dto.PopularItemsDto;
import com.ita.if103java.ims.dto.PopularItemsRequestDto;
import com.ita.if103java.ims.dto.WarehouseLoadDto;
import com.ita.if103java.ims.dto.WarehousePremiumStructDto;
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
    public WarehousePremiumStructDto getPreLoad(Long id, Long accountId) {
        return dashboardDao.getPreLoadByAccounId(id, accountId);
    }

    @Override
    public List<WarehouseLoadDto> getWarehouseLoad(Long accountID) {
        return dashboardDao.findWarehouseLoadByAccountId(accountID);
    }

    @Override
    public List<PopularItemsDto> getPopularItems(PopularItemsRequestDto popularItems) {
        return dashboardDao.findPopularItems(popularItems);
    }


    @Override
    public List<EndingItemsDto> getEndingItems(int minQuantity, Long accountId) {
        return dashboardDao.findEndedItemsByAccountId(minQuantity, accountId);
    }
}
