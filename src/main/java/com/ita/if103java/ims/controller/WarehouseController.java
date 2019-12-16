package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.mapper.WarehouseDtoMapper;
import com.ita.if103java.ims.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/warehouses")
public class WarehouseController {
    private WarehouseService warehouseService;
    private WarehouseDtoMapper mapper;

    @Autowired
    public WarehouseController(WarehouseService warehouseService, WarehouseDtoMapper mapper) {
        this.warehouseService = warehouseService;
        this.mapper = mapper;
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WarehouseDto findById(@PathVariable("id") Long id) {
        return warehouseService.findWarehouseById(id);
    }
}
