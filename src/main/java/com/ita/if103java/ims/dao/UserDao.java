package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface UserDao {

    User create(User user);

    User findById(Long id);

    User findAdminByAccountId(Long accountId);

    User update(User user);

    boolean updateAccountId(Long userId, Long accountId);

    boolean activate(Long id, Long accountId, boolean state);

    void hardDelete(Long accountId);

    List<User> findAll(Pageable pageable, Long accountId);

    Map<Long, String> findAllUserNames(Long accountId);

    Map<Long, String> findUserNamesById(List<Long> idList);

    User findByEmail(String email);

    void updatePassword(Long id, String newPassword);

    User findByEmailUUID(String emailUUID);

    Integer countOfUsers(Long accountId);
}
