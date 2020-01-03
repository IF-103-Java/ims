package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.EndingItemsDto;
import com.ita.if103java.ims.dto.PopularItemsDto;
import com.ita.if103java.ims.dto.PopularItemsRequestDto;
import com.ita.if103java.ims.dto.WarehouseLoadDto;
import com.ita.if103java.ims.dto.WarehousePremiumStructDto;
import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
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

    @GetMapping(value = "/warehouseLoad",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public List<WarehouseLoadDto> getWarehouseLoad(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return dashboardService.getWarehouseLoad(userDetails.getUser().getAccountId());
    }

    @GetMapping(value = "/premiumLoad",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public WarehousePremiumStructDto getPreLoad(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                @RequestParam Long id) {
            return dashboardService.getPreLoad(id, userDetails.getUser().getAccountId());
    }

    @PostMapping(value = "/popularityItems",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PopularItemsDto> getPopularItems(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                 @RequestBody PopularItemsRequestDto popularItems) {
        return dashboardService.getPopularItems(popularItems, userDetails.getUser().getAccountId());
    }

    @GetMapping(value = "/endingItems",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EndingItemsDto> getEndingItems(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @RequestParam int minQuantity) {
        return dashboardService.getEndingItems(minQuantity, userDetails.getUser().getAccountId());
    }
}
