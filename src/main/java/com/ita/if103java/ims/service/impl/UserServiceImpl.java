package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.UserDao;
import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.mapper.UserDtoMapper;
import com.ita.if103java.ims.service.MailService;
import com.ita.if103java.ims.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@PropertySource("classpath:application.properties")
public class UserServiceImpl implements UserService {

    @Value("${mail.activationURL}")
    private String activationURL;

    private UserDao userDao;
    private UserDtoMapper userDtoMapper;
    private MailService mailService;

    @Autowired
    public UserServiceImpl(UserDao userDao, UserDtoMapper userDtoMapper, MailService mailService) {
        this.userDao = userDao;
        this.userDtoMapper = userDtoMapper;
        this.mailService = mailService;
    }

    @Override
    public UserDto create(UserDto userDto) {
        User createdUser = userDao.create(userDtoMapper.convertUserDtoToUser(userDto));
        activationURL += createdUser.getEmailUUID();
        sendActivationMessage(userDto, activationURL);
        return userDtoMapper.convertUserToUserDto(createdUser);
    }

    @Override
    public UserDto findById(Long id) {
        return userDtoMapper.convertUserToUserDto(userDao.findById(id));
    }

    @Override
    public List<UserDto> findByAccountId(Long accountID) {
        return userDtoMapper.convertToUserDtoList(userDao.findByAccountId(accountID));
    }

    @Override
    public UserDto update(UserDto userDto) {
        User updatedUser = userDtoMapper.convertUserDtoToUser(userDto);
        //Activating status can't be changed in this way
        User DBUser = userDao.findById(updatedUser.getId());
        updatedUser.setActive(DBUser.isActive());
        return userDtoMapper.convertUserToUserDto(userDao.update(updatedUser));
    }

    @Override
    public boolean delete(Long id) {
        return userDao.softDelete(id);
    }

    @Override
    public List<UserDto> findAll() {
        return userDtoMapper.convertToUserDtoList(userDao.findAll());
    }

    @Override
    public UserDto findByEmail(String email) {
        return userDtoMapper.convertUserToUserDto(userDao.findByEmail(email));
    }

    @Override
    public boolean updatePassword(Long id, String newPassword) {
        return userDao.updatePassword(id, newPassword);
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
        }else {
            userDao.hardDelete(activatedUser.getId());
            return false;
        }
    }

    private void sendActivationMessage(UserDto userDto, String message) {
        mailService.sendMessage(userDto, "Please, activate your account:" + message, "Account activation");
    }
}
