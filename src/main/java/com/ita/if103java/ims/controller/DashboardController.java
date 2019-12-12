package com.ita.if103java.ims.controller;
import com.ita.if103java.ims.dto.PopularItemsRequestDto;
import com.ita.if103java.ims.dto.PopularItemsDto;
import com.ita.if103java.ims.dto.EndingItemsDto;
import com.ita.if103java.ims.dto.WarehouseLoadDto;
import com.ita.if103java.ims.dto.WarehousePremiumStructDto;
import com.ita.if103java.ims.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    private DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @RequestMapping(value = "/warehouseLoad")
    public List<WarehouseLoadDto> getWarehouseLoad(@RequestParam Long accountId){
        return dashboardService.getWarehouseLoad(accountId);
    }

    @RequestMapping(value="/premiumLoad")
    public WarehousePremiumStructDto getPreLoad(@RequestParam Long id,
                                                @RequestParam Long accountId){
        return dashboardService.getPreLoad(id, accountId);
    }

    @PostMapping(value = "/popularityItems",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PopularItemsDto> getPopularItems(@RequestBody PopularItemsRequestDto popularItems){
        return dashboardService.getPopularItems(popularItems);
    }

    @RequestMapping(value = "/endingItems")
    public List<EndingItemsDto> getEndingItems(@RequestParam int minQuantity,
                                               @RequestParam Long accountId){
        return dashboardService.getEndingItems(minQuantity, accountId);
    }
}
