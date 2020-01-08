package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.UserDao;
import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.mapper.dto.UserDtoMapper;
import com.ita.if103java.ims.service.EventService;
import com.ita.if103java.ims.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static com.ita.if103java.ims.entity.EventName.PASSWORD_CHANGED;
import static com.ita.if103java.ims.entity.EventName.PROFILE_CHANGED;
import static com.ita.if103java.ims.entity.EventName.SIGN_UP;
import static com.ita.if103java.ims.util.TokenUtil.isValidToken;
import static com.ita.if103java.ims.util.UserEventUtil.createEvent;

@Service
@PropertySource("classpath:application.properties")
public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private UserDtoMapper mapper;
    private PasswordEncoder passwordEncoder;
    private EventService eventService;


    @Autowired
    public UserServiceImpl(UserDao userDao,
                           UserDtoMapper mapper,
                           PasswordEncoder passwordEncoder,
                           EventService eventService) {
        this.userDao = userDao;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.eventService = eventService;
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = mapper.toEntity(userDto);

        ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.systemDefault());
        if (user.getRole() != Role.ROLE_WORKER) {
            user.setRole(Role.ROLE_ADMIN);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedDate(currentDateTime);
        user.setUpdatedDate(currentDateTime);
        user.setEmailUUID(UUID.randomUUID().toString());

        User createdUser = userDao.create(user);
        eventService.create(createEvent(createdUser, SIGN_UP, "signed up."));
        return mapper.toDto(createdUser);
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
        updatedUser.setPassword(passwordEncoder.encode(userDto.getPassword()));

        User user = userDao.update(updatedUser);
        eventService.create(createEvent(user, PROFILE_CHANGED, "updated profile."));
        return mapper.toDto(user);
    }

    @Override
    public boolean delete(Long id) {
        return userDao.softDelete(id);
    }

    @Override
    public List<UserDto> findAll(Pageable pageable) {
        return mapper.toDtoList(userDao.findAll(pageable));
    }

    @Override
    public UserDto findByEmail(String email) {
        return mapper.toDto(userDao.findByEmail(email));
    }

    @Override
    public boolean updatePassword(Long id, String newPassword) {
        if (userDao.updatePassword(id, passwordEncoder.encode(newPassword))) {
            User user = userDao.findById(id);
            eventService.create(createEvent(user, PASSWORD_CHANGED, "changed the password."));
            return true;
        }
        return false;
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
