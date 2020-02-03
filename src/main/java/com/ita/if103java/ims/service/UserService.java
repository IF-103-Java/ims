package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.security.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto createAndSendMessage(UserDto userDto);

    UserDto findById(Long id);

    List<UserDto> findWorkersByAccountId(Long accountID);

    UserDto findAdminByAccountId(Long accountID);

    UserDto update(UserDto userDto);

    boolean delete(Long id, Long accountId);

    Page<UserDto> findAll(Pageable pageable, Long accountId);

    UserDto findByEmail(String email);

    boolean updatePassword(Long id, String newPassword);

    boolean activateUser(String emailUUID);

    Map<Long, String> findAllUserNames(UserDetailsImpl user);

}
