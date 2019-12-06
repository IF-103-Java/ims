package com.ita.if103java.ims.controller;
import com.ita.if103java.ims.dto.PopularBodyDto;
import com.ita.if103java.ims.dto.PopularityListDto;
import com.ita.if103java.ims.dto.RefillListDto;
import com.ita.if103java.ims.dto.WarehouseLoadDto;
import com.ita.if103java.ims.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @RequestMapping(value = "/warehouseWidget")
    public List<WarehouseLoadDto> getWarehouseWidget(){
        return dashboardService.getWarehouseWidget();
    }

    @PostMapping(value = "/popularityList",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PopularityListDto> getPopularList(@RequestBody PopularBodyDto popularBodyDto){
        return dashboardService.getPopularityList(
            popularBodyDto.getQuantity(),
            popularBodyDto.getDateType(),
            popularBodyDto.getPopType(),
            popularBodyDto.getDate()
        );
    }

    @RequestMapping(value = "/refillList")
    public List<RefillListDto> getRefillList(@RequestParam int minQuantity){
        return dashboardService.getRefillList(minQuantity);
    }
}
