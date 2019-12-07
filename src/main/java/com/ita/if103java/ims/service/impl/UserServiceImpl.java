package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.UserDao;
import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.mapper.UserDtoMapper;
import com.ita.if103java.ims.service.MailService;
import com.ita.if103java.ims.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@PropertySource("classpath:application.properties")
public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private UserDtoMapper mapper;
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public UserServiceImpl(UserDao userDao, UserDtoMapper mapper, MailService mailService) {
        this.userDao = userDao;
        this.mapper = mapper;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();

    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = mapper.toEntity(userDto);

        ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.systemDefault());
        String emailUUID = UUID.randomUUID().toString();
        String encryptedPassword = "";
        Role role = user.getRole();
        if (role == null) {
            encryptedPassword = bCryptPasswordEncoder.encode(user.getPassword());
            role = Role.ADMIN;
        }

        user.setPassword(encryptedPassword);
        user.setRole(role);
        user.setCreatedDate(currentDateTime);
        user.setUpdatedDate(currentDateTime);
        user.setEmailUUID(emailUUID);

        return mapper.toDto(user);
    }

    @Override
    public UserDto findById(Long id) {
        return mapper.toDto(userDao.findById(id));
    }

    @Override
    public List<UserDto> findUsersByAccountId(Long accountID) {
        return mapper.toDtoList(userDao.findUsersByAccountId(accountID));
    }

    @Override
    public UserDto findAdminByAccountId(Long accountID) {
        return mapper.toDto(userDao.findAdminByAccountId(accountID));
    }

    @Override
    public UserDto update(UserDto userDto) {
        User updatedUser = mapper.toEntity(userDto);
        //Activating status can't be changed in this way
        User DBUser = userDao.findById(updatedUser.getId());
        updatedUser.setActive(DBUser.isActive());
        return mapper.toDto(userDao.update(updatedUser));
    }

    @Override
    public boolean delete(Long id) {
        return userDao.softDelete(id);
    }

    @Override
    public List<UserDto> findAll() {
        return mapper.toDtoList(userDao.findAll());
    }

    @Override
    public UserDto findByEmail(String email) {
        return mapper.toDto(userDao.findByEmail(email));
    }

    @Override
    public boolean updatePassword(Long id, String newPassword) {
        return userDao.updatePassword(id, bCryptPasswordEncoder.encode(newPassword));
    }

    @Override
    public boolean activateUser(String emailUUId) {
        User activatedUser = userDao.findByEmailUUID(emailUUId);
        ZonedDateTime updatedDateTime = activatedUser.getUpdatedDate();
        ZonedDateTime currrentDateTime = ZonedDateTime.now(ZoneId.systemDefault());

        Duration duration = Duration.between(updatedDateTime, currrentDateTime);
        long diffHours = Math.abs(duration.toHours());
        if (diffHours <= 24) {
            activatedUser.setActive(true);
            userDao.update(activatedUser);
            return true;
        } else {
            userDao.hardDelete(activatedUser.getId());
            return false;
        }
    }

}
