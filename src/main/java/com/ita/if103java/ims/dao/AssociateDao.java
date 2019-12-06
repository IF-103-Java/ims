package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.Associate;

import java.util.List;

public interface AssociateDao {

    Associate create(Associate associate);

    Associate findById(Long id);

    List<Associate> findByAccountId(Long accountId);

    Associate findByEmail(String email);

    List<Associate> findAll();

    Associate update(Associate associate);

    boolean delete(Long id);
}
