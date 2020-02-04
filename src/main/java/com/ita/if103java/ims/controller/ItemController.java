package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ItemDto addItem(@RequestBody ItemDto itemDto, @AuthenticationPrincipal UserDetailsImpl user) {
        return itemService.addItem(itemDto, user);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ItemDto> sort(Pageable pageable, @AuthenticationPrincipal UserDetailsImpl user) {
        return itemService.findSortedItems(pageable, user);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto findById(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetailsImpl user) {
        return itemService.findById(id, user);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public boolean softDelete(@PathVariable("itemId") Long id, @AuthenticationPrincipal UserDetailsImpl user) {
        return itemService.softDelete(id, user);
    }

    @GetMapping("/name")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> findItemsByNameQuery(@RequestParam("q") String query,
                                              @AuthenticationPrincipal UserDetailsImpl user) {
        return itemService.findItemsByNameQuery(query, user);
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @AuthenticationPrincipal UserDetailsImpl user) {
        System.out.println("up");
        return itemService.updateItem(itemDto, user);
    }
}

