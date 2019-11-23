package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.User;

import java.util.List;

public interface UserDao {

    User create(User user);

    User findById(Long id);

    User findByAccountId(Long accountID);

    User update(User user);

    boolean delete(Long id);

    List<User> findAll(int from, int to);

    User findByEmail(String email);

    boolean updatePassword(Long id, String newPassword);
}
