package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.dto.ItemTransactionRequestDto;
import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.security.UserDetailsImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService {

    List<ItemDto> findSortedItems(Pageable pageable, UserDetailsImpl user);

    ItemDto findById(Long id, UserDetailsImpl user);

    SavedItemDto addSavedItem(ItemTransactionRequestDto itemTransaction, UserDetailsImpl user);

    ItemDto addItem(ItemDto itemDto, UserDetailsImpl user);

    SavedItemDto findSavedItemById(Long id, UserDetailsImpl user);

    boolean softDelete(Long id, UserDetailsImpl user);

    List<SavedItemDto> findByItemId(Long id, UserDetailsImpl user);

    List<WarehouseDto> findUsefulWarehouses(int volume, int quantity, UserDetailsImpl user);

    boolean moveItem(ItemTransactionRequestDto itemTransaction, UserDetailsImpl user);

    SavedItemDto outcomeItem(ItemTransactionRequestDto itemTransaction, UserDetailsImpl user);

}
