package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.Associate;
import com.ita.if103java.ims.entity.AssociateType;

import java.util.List;

public interface AssociateDao {

    Associate create(Long accountId, Associate associate);

    Associate findById(Long accountId, Long id);

    List<Associate> findByAccountId(Long accountId);

    List<Associate> getAssociates(String sort, int size, long offset, long accountId);

    Associate update(Long accountId, Associate associate);

    boolean delete(Long accountId, Long id);

    List<Associate> getAssociatesByNameAndType(Long accountId, String name, AssociateType type);
}
