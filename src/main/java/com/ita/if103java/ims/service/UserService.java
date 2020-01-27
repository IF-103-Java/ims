package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.security.UserDetailsImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto createAndSendMessage(UserDto userDto);

    UserDto findById(Long id);

    List<UserDto> findUsersByAccountId(Long accountID);

    List<UserDto> findWorkersByAccountId(Long accountID);

    UserDto findAdminByAccountId(Long accountID);

    UserDto update(UserDto userDto);

    boolean delete(Long id);

    List<UserDto> findAll(Pageable pageable);

    UserDto findByEmail(String email);

    boolean updatePassword(Long id, String newPassword);

    boolean activateUser(String emailUUID);

    Map<Long, String> findUserNames(UserDetailsImpl user);

}
