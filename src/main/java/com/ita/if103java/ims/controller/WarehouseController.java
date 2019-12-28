package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public WarehouseDto add(@RequestBody WarehouseDto warehouseDto, @AuthenticationPrincipal UserDetailsImpl user) {
        return warehouseService.add(warehouseDto,user);
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WarehouseDto findById(@PathVariable("id") Long id) {
        return warehouseService.findWarehouseById(id);
    }

    @GetMapping(value = "/")
    @ResponseStatus(HttpStatus.OK)
    public List<WarehouseDto> findAll(Pageable pageable) {
        return warehouseService.findAll(pageable);
    }

    @GetMapping(value = "/topWarehouseId/{id}")
    @ResponseStatus(HttpStatus.OK)
    List<WarehouseDto> findWarehousesByTopLevelId(@PathVariable("topWarehouseId") Long topWarehouseId) {
        return warehouseService.findWarehousesByTopLevelId(topWarehouseId);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WarehouseDto update(@RequestBody WarehouseDto warehouseDto, @PathVariable("id") Long id) {
        warehouseDto.setId(id);
        return warehouseService.update(warehouseDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long id) {
        warehouseService.softDelete(id);
    }

}
