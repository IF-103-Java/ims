package com.ita.if103java.ims.controller;


import com.ita.if103java.ims.dto.WarehouseItemAdviceDto;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.WarehouseItemAdviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/warehouse-advice")
public class WarehouseAdviceController {

    private final WarehouseItemAdviceService warehouseItemAdviceService;

    @Autowired
    public WarehouseAdviceController(WarehouseItemAdviceService warehouseItemAdviceService) {
        this.warehouseItemAdviceService = warehouseItemAdviceService;
    }

    @GetMapping("/{id}")
    public WarehouseItemAdviceDto findByItemId(@PathVariable("id") Long id,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return warehouseItemAdviceService.getAdvice(id, userDetails);
    }
}
