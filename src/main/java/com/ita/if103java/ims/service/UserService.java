package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.ResetPasswordDto;
import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.security.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto createAndSendMessage(UserDto userDto);

    UserDto findById(Long id);

    UserDto findAdminByAccountId(Long accountID);

    UserDto update(UserDto userDto);

    boolean delete(Long id, Long accountId);

    Page<UserDto> findAll(Pageable pageable, Long accountId);

    UserDto findByEmail(String email);

    boolean updatePassword(UserDto userDao, ResetPasswordDto resetPasswordDto);

    boolean activateUser(String emailUUID);

    Map<Long, String> findAllUserNames(UserDetailsImpl user);

    boolean isAllowedToInvite(Long accountId);
}
