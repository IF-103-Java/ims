package com.ita.if103java.ims.controller;

import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
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
    public ItemDto addItem(@RequestBody ItemDto itemDto, @AuthenticationPrincipal User user) {
        return itemService.addItem(itemDto, user);
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> sort(Pageable pageable) {
        return itemService.findSortedItem(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
            pageable.getSort()));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto findById(@PathVariable("id") Long id) {
        return itemService.findById(id);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public boolean softDelete(@PathVariable("itemId") Long id) {
        return itemService.softDelete(id);
    }

}
