package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.UserDao;
import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.mapper.UserDtoMapper;
import com.ita.if103java.ims.service.MailService;
import com.ita.if103java.ims.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${mail.activationURL}")
    private String activationURL;

    private UserDao userDao;
    private UserDtoMapper userDtoMapper;
    private MailService mailService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public UserServiceImpl(UserDao userDao, UserDtoMapper userDtoMapper, MailService mailService) {
        this.userDao = userDao;
        this.userDtoMapper = userDtoMapper;
        this.mailService = mailService;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();

    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = userDtoMapper.convertUserDtoToUser(userDto);

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

        User createdUser = userDao.create(user);
        sendActivationMessage(userDtoMapper.convertUserToUserDto(createdUser), activationURL + emailUUID);
        return userDtoMapper.convertUserToUserDto(user);
    }

    @Override
    public UserDto findById(Long id) {
        return userDtoMapper.convertUserToUserDto(userDao.findById(id));
    }

    @Override
    public List<UserDto> findUsersByAccountId(Long accountID) {
        return userDtoMapper.convertToUserDtoList(userDao.findUsersByAccountId(accountID));
    }

    @Override
    public UserDto findUserByAccountId(Long accountID) {
        return userDtoMapper.convertUserToUserDto(userDao.findUserByAccountId(accountID));
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

    private void sendActivationMessage(UserDto userDto, String activationURL) {
        String message = "" +
            "Hello, we are happy to see you in our Inventory Management System.\n" +
            "Please, follow link bellow to activate your account:\n";
        mailService.sendMessage(userDto, message + activationURL, "Account activation");
    }
}
