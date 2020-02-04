package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.Item;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ItemDao {
    List<Item> getItems(long accountId, int size, long offset, Sort sort);

    Integer countItemsById(long accountId);

    Item findItemByName(String name);

    List<Item> findItemByAccountId(Long id);

    Item findItemById(Long id, Long accountId);

    boolean isExistItemById(Long id, Long accountId);

    Item addItem(Item item);

    boolean softDeleteItem(Long id, Long accountId);

    List<Item> findItemsByNameQuery(String query, long accountId);

    Item updateItem(Item item);
}
