package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.*;
import com.ita.if103java.ims.entity.Associate;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/savedItems")
public class SavedItemController {
    private ItemService itemService;

    @Autowired
    public SavedItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public SavedItemDto addSavedItem(@RequestBody SavedItemDto savedItemDto, @AuthenticationPrincipal User user, @AuthenticationPrincipal Associate associate) {
        return itemService.addSavedItem(savedItemDto, user, associate);
    }

    @GetMapping("/usefullWarehouses")
    @ResponseStatus(HttpStatus.OK)
    public List<WarehouseDto> findUsefullWarehouses(@RequestParam("volume") int volume,
                                                    @RequestParam("capacity") int capacity) {
        return itemService.findUsefullWarehouses(volume, capacity);
    }

    @GetMapping(path = "/itemId/{itemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<SavedItemDto> findByItemDto(@PathVariable("itemId") Long id) {
        return itemService.findByItemId(id);
    }

    @GetMapping("/{savedItemId}")
    @ResponseStatus(HttpStatus.OK)
    public SavedItemDto findSavedItemById(@PathVariable("savedItemId") Long id) {
        return itemService.findSavedItemById(id);
    }

    @PutMapping(value = "/move", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public boolean moveSavedItem(@RequestBody SavedItemDto savedItemDto, @RequestParam("warehouseId") Long id, @AuthenticationPrincipal User user, @AuthenticationPrincipal Associate associate) {
        return itemService.moveItem(savedItemDto, id, user, associate);
    }

    @PutMapping(value = "/outcome", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public SavedItemDto outcomeItem(@RequestBody SavedItemDto savedItemDto, @RequestParam("quantity") int quantity, @AuthenticationPrincipal User user, @AuthenticationPrincipal Associate associate) {
        return itemService.outcomeItem(savedItemDto, quantity, user, associate);
    }

}