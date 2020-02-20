package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.UsefulWarehouseDto;
import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import java.util.Map;

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
    public WarehouseDto add(@RequestBody WarehouseDto warehouseDto,
                            @AuthenticationPrincipal UserDetailsImpl user) {
        return warehouseService.add(warehouseDto, user);
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WarehouseDto findById(@PathVariable("id") Long id,
                                 @AuthenticationPrincipal UserDetailsImpl user) {
        return warehouseService.findById(id, user);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<WarehouseDto> findAll(Pageable pageable,
                                      @AuthenticationPrincipal UserDetailsImpl user) {
        return warehouseService.findAllTopLevel(pageable, user);
    }

    @GetMapping(value = "/topWarehouseId/{topWarehouseId}")
    @ResponseStatus(HttpStatus.OK)
    List<WarehouseDto> findWarehousesByTopLevelId(@PathVariable("topWarehouseId") Long topWarehouseId,
                                                  @AuthenticationPrincipal UserDetailsImpl user) {
        return warehouseService.findWarehousesByTopLevelId(topWarehouseId, user);
    }

    @PutMapping("/update/")
    @ResponseStatus(HttpStatus.OK)
    public WarehouseDto update(@RequestBody WarehouseDto warehouseDto,
                               @AuthenticationPrincipal UserDetailsImpl user) {

        return warehouseService.update(warehouseDto, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long id,
                       @AuthenticationPrincipal UserDetailsImpl user) {
        warehouseService.softDelete(id, user);
    }

    @GetMapping("/warehousenames")
    public Map<Long, String> getWarehouseNames(@AuthenticationPrincipal UserDetailsImpl user) {
        return warehouseService.findAllWarehouseNames(user);
    }

    @GetMapping(value = "/children/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<WarehouseDto> findChildrenById(@PathVariable("id") Long id,
                                               @AuthenticationPrincipal UserDetailsImpl user) {
        return warehouseService.findChildrenById(id, user);
    }

    @GetMapping(value = "/capacity/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Integer findTotalCapacity(@PathVariable("id") Long id,
                                     @AuthenticationPrincipal UserDetailsImpl user) {
        return warehouseService.findTotalCapacity(id, user);
    }

    @GetMapping("/topwarehouses")
    public List<WarehouseDto> getAllTopLevelList(@AuthenticationPrincipal UserDetailsImpl user) {
        return warehouseService.findAllTopLevelList(user);

    }

    @GetMapping(path = "/usefulWarehouses/{capacity}")
    public List<UsefulWarehouseDto> findUsefulWarehouses(@PathVariable("capacity") Long capacity,
                                                         @AuthenticationPrincipal UserDetailsImpl user) {
        return warehouseService.findUsefulWarehouses(capacity, user);
    }
}
