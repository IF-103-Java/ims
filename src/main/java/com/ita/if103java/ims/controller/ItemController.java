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
import org.springframework.web.bind.annotation.PutMapping;
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
    @PostMapping("/addItem")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto addItem(@RequestBody ItemDto itemDto) {
        return itemService.addItem(itemDto);
    }
    @PostMapping("/addSavedItem")
    @ResponseStatus(HttpStatus.OK)
    public SavedItemDto addSavedItem(@RequestBody SavedItemDto savedItemDto) {
        return itemService.addSavedItem(savedItemDto);
    }
    @GetMapping("/getAllItems")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getItems() {
        return itemService.findItems();
    }

    @GetMapping("/getItemsByParam")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> findItemsByParam(@RequestParam("param") String param) {
        return itemService.findItemsByParam(param);
    }

    @GetMapping("/getUsefullWarehouses")
    @ResponseStatus(HttpStatus.OK)
    public List<WarehouseDto> findUsefullWarehouses(@RequestParam("savedItemDto") SavedItemDto savedItemDto) {
        return itemService.findUsefullWarehouses(savedItemDto);
    }

    @GetMapping("/getItemById")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto findById(@RequestParam("id") Long id) {
        return itemService.findById(id);
    }
    @GetMapping("/getSavedItemByItemDto")
    @ResponseStatus(HttpStatus.OK)
    public SavedItemDto findByItemDto(ItemDto itemDto) {
        return itemService.findByItemDto(itemDto);
    }
    @GetMapping("/getSavedItemById")
    @ResponseStatus(HttpStatus.OK)
    public SavedItemDto findSavedItemById(@RequestParam("savedItemDto") SavedItemDto savedItemDto) {
        return itemService.findSavedItemById(savedItemDto);
    }

    @PutMapping(value = "/moveItem", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public boolean moveItem(@RequestBody WarehouseDto warehouseDto, @RequestBody SavedItemDto savedItemDto) {
        return itemService.moveItem(warehouseDto, savedItemDto);
    }

    @PutMapping(value = "/outcomeItem", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public SavedItemDto outcomeItem(@RequestBody SavedItemDto savedItemDto, @RequestBody int quantity) {
        return itemService.outcomeItem(savedItemDto, quantity);
    }

    @DeleteMapping("/softDeleteItem")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public boolean softDelete(@RequestParam("itemDto") ItemDto itemDto) {
        return itemService.softDelete(itemDto);
    }

}
