package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface UserDao {

    User create(User user);

    User findById(Long id);

    List<User> findUsersByAccountId(Long accountId);

    User findAdminByAccountId(Long accountId);

    User update(User user);

    boolean updateAccountId(Long userId, Long accountId);

    boolean softDelete(Long id);

    boolean hardDelete(Long id);

    List<User> findAll(Pageable pageable);

    Map<Long, String> findUsernames(Long accountId);

    Map<Long, String> findUsernames(List<Long> idList);

    User findByEmail(String email);

    boolean updatePassword(Long id, String newPassword);

    User findByEmailUUID(String emailUUID);

    Integer countOfUsers(Long accountId);
}
