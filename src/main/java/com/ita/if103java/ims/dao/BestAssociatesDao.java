package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.AssociateAddressTotalTransactionQuantity;

import java.util.List;

public interface BestAssociatesDao {
    List<AssociateAddressTotalTransactionQuantity> findByItem(Long accountId, Long itemId, Integer limit);
}
