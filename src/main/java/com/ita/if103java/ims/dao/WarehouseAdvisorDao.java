package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.AssociateAddressTotalTransactionQuantity;

import java.util.List;

public interface WarehouseAdvisorDao {
    List<AssociateAddressTotalTransactionQuantity> findBestAssociatesByItem(Long accountId, Long itemId, Integer limit);
}
