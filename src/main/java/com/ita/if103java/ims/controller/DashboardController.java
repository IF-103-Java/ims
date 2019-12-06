package com.ita.if103java.ims.controller;
import com.ita.if103java.ims.dto.PopularityListDto;
import com.ita.if103java.ims.dto.RefillListDto;
import com.ita.if103java.ims.dto.WarehouseLoadDto;
import com.ita.if103java.ims.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @RequestMapping(value = "/wh")
    public List<WarehouseLoadDto> getWarehouseWidget(){
        return dashboardService.getWarehouseWidget();
    }

    @RequestMapping(value = "/pl")
    public List<PopularityListDto> getPopularList(@RequestParam int quantity, @RequestParam String type,
                                                  @RequestParam String date){
        return dashboardService.getPopularList(quantity, type, date);
    }

    @RequestMapping(value = "/ul")
    public List<PopularityListDto> getUnpopularList(@RequestParam int quantity, @RequestParam String type,
                                                    @RequestParam String date){
        return dashboardService.getUnpopularList(quantity,type, date);
    }

    @RequestMapping(value = "/rl")
    public List<RefillListDto> getRefillList(@RequestParam int minQuantity){
        return dashboardService.getRefillList(minQuantity);
    }
}
