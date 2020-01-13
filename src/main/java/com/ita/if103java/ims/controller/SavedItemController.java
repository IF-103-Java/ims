package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.ItemTransactionRequestDto;
import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
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
    public SavedItemDto addSavedItem(@RequestBody ItemTransactionRequestDto itemTransaction,
                                     @AuthenticationPrincipal UserDetailsImpl user) {
        return itemService.addSavedItem(itemTransaction, user);
    }

    @GetMapping("/usefulWarehouses")
    @ResponseStatus(HttpStatus.OK)
    public List<WarehouseDto> findUsefulWarehouses(@RequestParam("volume") int volume,
                                                   @RequestParam("capacity") int capacity,
                                                   @AuthenticationPrincipal UserDetailsImpl user) {
        return itemService.findUsefulWarehouses(volume, capacity, user);
    }

    @GetMapping(path = "/itemId/{itemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<SavedItemDto> findByItemId(@PathVariable("itemId") Long id,
                                           @AuthenticationPrincipal UserDetailsImpl user) {
        return itemService.findByItemId(id, user);
    }

    @GetMapping("/{savedItemId}")
    @ResponseStatus(HttpStatus.OK)
    public SavedItemDto findSavedItemById(@PathVariable("savedItemId") Long id,
                                          @AuthenticationPrincipal UserDetailsImpl user) {
        return itemService.findSavedItemById(id, user);
    }

    @PutMapping(value = "/move", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public boolean moveSavedItem(@RequestBody ItemTransactionRequestDto itemTransaction,
                                 @AuthenticationPrincipal UserDetailsImpl user) {
        return itemService.moveItem(itemTransaction, user);
    }

    @PutMapping(value = "/outcome", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public SavedItemDto outcomeItem(@RequestBody ItemTransactionRequestDto itemTransaction,
                                    @AuthenticationPrincipal UserDetailsImpl user) {
        return itemService.outcomeItem(itemTransaction, user);
    }

}
