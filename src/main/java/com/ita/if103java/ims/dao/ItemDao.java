package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.Item;

import java.util.List;

public interface ItemDao {
    List<Item> getItems();

    Item findItemByName(String name);

    Item findItemByAccountId(Long id);

    Item addItem(Item item);

    boolean softDeleteItem(String name);
}
