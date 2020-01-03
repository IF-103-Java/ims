package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.Item;


import java.util.List;

public interface ItemDao {
    List<Item> getItems(String sort, int size, long offset, long accountId);

    Item findItemByName(String name);

    List<Item> findItemByAccountId(Long id);

    Item findItemById(Long id);

    boolean isExistItemById(Long id, Long accountId);

    Item addItem(Item item);

    boolean softDeleteItem(Long id, Long accountId);
}
