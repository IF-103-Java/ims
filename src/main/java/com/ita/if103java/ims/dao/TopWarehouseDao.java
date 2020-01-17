package com.ita.if103java.ims.dao;

import java.util.List;

public interface TopWarehouseDao {
    List<Long> findAllIds(Long accountId);
}
