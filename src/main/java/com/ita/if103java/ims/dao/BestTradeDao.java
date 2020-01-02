package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.TotalTransactionQuantity;

import java.util.List;

public interface BestTradeDao {
    List<TotalTransactionQuantity> findBestClientsByItemId(Long itemId, Integer limit);

    List<TotalTransactionQuantity> findBestSuppliersByItemId(Long itemId, Integer limit);
}
