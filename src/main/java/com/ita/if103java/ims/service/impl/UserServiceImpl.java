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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static com.ita.if103java.ims.util.TokenUtil.isValidToken;

@Service
@PropertySource("classpath:application.properties")
public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private UserDtoMapper mapper;
    private PasswordEncoder passwordEncoder;


    @Autowired
    public UserServiceImpl(UserDao userDao, UserDtoMapper mapper, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = mapper.toEntity(userDto);

        ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.systemDefault());
        String emailUUID = UUID.randomUUID().toString();
        String encryptedPassword = "";

        if (user.getRole() != Role.WORKER){
            encryptedPassword = passwordEncoder.encode(user.getPassword());
            user.setRole(Role.ADMIN);
        }

        user.setPassword(encryptedPassword);
        user.setCreatedDate(currentDateTime);
        user.setUpdatedDate(currentDateTime);
        user.setEmailUUID(emailUUID);

        return mapper.toDto(userDao.create(user));
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

        User dbUser = userDao.findById(updatedUser.getId());
        updatedUser.setActive(dbUser.isActive());
        updatedUser.setUpdatedDate(ZonedDateTime.now(ZoneId.systemDefault()));

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
        return userDao.updatePassword(id, passwordEncoder.encode(newPassword));
    }

    @Override
    public boolean activateUser(String emailUUID) {
        User activatedUser = userDao.findByEmailUUID(emailUUID);
        if (isValidToken(activatedUser)) {
            activatedUser.setActive(true);
            userDao.update(activatedUser);
            return true;
        } else {
            userDao.hardDelete(activatedUser.getId());
            return false;
        }
    }



}
