package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.mapper.WarehouseDtoMapper;
import com.ita.if103java.ims.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/warehouses")
public class WarehouseController {
    private WarehouseService warehouseService;

    @Autowired
    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping(value = "/add")
    @ResponseStatus(HttpStatus.OK)
    public WarehouseDto add(@RequestBody WarehouseDto warehouseDto) {
        return warehouseService.add(warehouseDto);
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WarehouseDto findById(@PathVariable("id") Long id) {
        return warehouseService.findWarehouseById(id);
    }

    @GetMapping(value = "/")
    @ResponseStatus(HttpStatus.OK)
    public List<WarehouseDto> findAll() {
        return warehouseService.findAll();
    }

    @GetMapping(value = "/topWarehouseId{id}")
    @ResponseStatus(HttpStatus.OK)
    List<WarehouseDto> findWarehousesByTopLevelId(@PathVariable("topWarehouseId") Long topWarehouseId){
        return warehouseService.findWarehousesByTopLevelId(topWarehouseId);
    }

    @PutMapping("/update{id}")
    @ResponseStatus(HttpStatus.OK)
    public WarehouseDto update(@RequestBody WarehouseDto warehouseDto, @PathVariable("id") Long id) {
        warehouseDto.setId(id);
        return warehouseService.update(warehouseDto);
    }

    @DeleteMapping("/delete{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long id) {
        warehouseService.softDelete(id);
    }

}
