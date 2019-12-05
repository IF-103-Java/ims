package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto findById(Long id);

    List<UserDto> findUsersByAccountId(Long accountID);

    UserDto findAdminByAccountId(Long accountID);

    UserDto update(UserDto userDto);

    boolean delete(Long id);

    List<UserDto> findAll();

    UserDto findByEmail(String email);

    boolean updatePassword(Long id, String newPassword);

    boolean activateUser(String emailUUID);

}
