package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.*;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ItemService {

    List<ItemDto> findSortedItem(Pageable pageable);

    ItemDto findById(Long id);

    SavedItemDto addSavedItem(SavedItemDto savedItemDto, UserDto user, AssociateDto associate);

    ItemDto addItem(ItemDto itemDto, UserDto user);

    SavedItemDto findSavedItemById(Long id);

    boolean softDelete(Long id);

    List<SavedItemDto> findByItemId(Long id);

    List<WarehouseDto> findUsefullWarehouses(int volume, int quantity);

    boolean moveItem(SavedItemDto savedItemDto, Long id, UserDto user, AssociateDto associate);

    SavedItemDto outcomeItem(SavedItemDto savedItemDto, int quantity, UserDto user, AssociateDto associate);

}
