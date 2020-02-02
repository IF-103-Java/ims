package com.ita.if103java.ims.controller;


import com.ita.if103java.ims.dto.warehouse.advice.WarehouseItemAdviceDto;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.WarehouseAdvisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/warehouse-advice")
public class WarehouseAdviceController {

    private final WarehouseAdvisorService warehouseAdvisorService;

    @Autowired
    public WarehouseAdviceController(WarehouseAdvisorService warehouseAdvisorService) {
        this.warehouseAdvisorService = warehouseAdvisorService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ITEM_STORAGE_ADVISOR')")
    public WarehouseItemAdviceDto findByItemId(@PathVariable("id") Long id,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return warehouseAdvisorService.getAdvice(userDetails.getUser().getAccountId(), id);
    }
}
