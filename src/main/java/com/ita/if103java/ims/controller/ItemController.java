package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/items"})
public class ItemController {
    private ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/getAllItems")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getItems() {
        return itemService.findItems();
    }

    @GetMapping("/getItemByParam")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> findItemsByParam(@RequestParam("param") String param) {
        return itemService.findItemsByParam(param);
    }

    @PostMapping("/addSavedItem")
    @ResponseStatus(HttpStatus.OK)
    public SavedItemDto addSavedItem(@RequestBody SavedItemDto savedItemDto) {
        return itemService.addSavedItem(savedItemDto);
    }

    @GetMapping("/getWarehouses")
    @ResponseStatus(HttpStatus.OK)
    public List<WarehouseDto> findUsefullWarehouses(@RequestParam("savedItemDto") SavedItemDto savedItemDto) {
        return itemService.findUsefullWarehouses(savedItemDto);
    }

    @PostMapping("/addItem")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto addItem(@RequestBody ItemDto itemDto) {
        return itemService.addItem(itemDto);
    }

    @GetMapping("/getSavedItemById")
    @ResponseStatus(HttpStatus.OK)
    public SavedItemDto findSavedItemById(@RequestParam("savedItemDto") SavedItemDto savedItemDto) {
        return itemService.findSavedItemById(savedItemDto);
    }

    @PostMapping(value = "/moveItem", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public boolean moveItem(@RequestBody WarehouseDto warehouseDto, @RequestBody SavedItemDto savedItemDto) {
        return itemService.moveItem(warehouseDto, savedItemDto);
    }

    @PostMapping(value = "/outcomeItem", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public SavedItemDto outcomeItem(@RequestBody SavedItemDto savedItemDto, @RequestBody int quantity) {
        return itemService.outcomeItem(savedItemDto, quantity);
    }

    @DeleteMapping("/softDeleteItem")
    @ResponseStatus(HttpStatus.OK)
    public boolean softDelete(@RequestParam("itemDto") ItemDto itemDto) {
        return itemService.softDelete(itemDto);
    }

}
