package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.UserDao;
import com.ita.if103java.ims.dto.AccountDto;
import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.mapper.dto.UserDtoMapper;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.AccountService;
import com.ita.if103java.ims.service.EventService;
import com.ita.if103java.ims.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.ita.if103java.ims.config.MailMessagesConfig.ACTIVATE_USER;
import static com.ita.if103java.ims.config.MailMessagesConfig.FOOTER;
import static com.ita.if103java.ims.entity.EventName.PASSWORD_CHANGED;
import static com.ita.if103java.ims.entity.EventName.PROFILE_CHANGED;
import static com.ita.if103java.ims.util.TokenUtil.isValidToken;
import static com.ita.if103java.ims.util.UserEventUtil.createEvent;

@Service
@PropertySource("classpath:application.properties")
public class UserServiceImpl implements UserService {

    @Value("${mail.activationURL}")
    private String activationURL;

    private UserDao userDao;
    private UserDtoMapper mapper;
    private PasswordEncoder passwordEncoder;
    private EventService eventService;
    private AccountService accountService;
    private MailServiceImpl mailService;


    @Autowired
    public UserServiceImpl(UserDao userDao,
                           UserDtoMapper mapper,
                           PasswordEncoder passwordEncoder,
                           EventService eventService,
                           AccountService accountService,
                           MailServiceImpl mailService) {
        this.userDao = userDao;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.eventService = eventService;
        this.accountService = accountService;
        this.mailService = mailService;
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
        return mapper.toDto(createdUser);
    }

    @Override
    @Transactional
    public UserDto createAndSendMessage(UserDto userDto) {
        UserDto createdUser = create(userDto);
        accountService.create(createdUser, userDto.getAccountName());
        String token = createdUser.getEmailUUID();
        String message = "" +
            ACTIVATE_USER
            + activationURL + token + "\n" +
            FOOTER;
        mailService.sendMessage(createdUser, message, "ACCOUNT ACTIVATION");
        return createdUser;
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

    @Transactional
    @Override
    public UserDto update(UserDto userDto) {
        User user = userDao.findById(userDto.getId());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        User updatedUser = userDao.update(user);
        user.setUpdatedDate(updatedUser.getUpdatedDate());
        eventService.create(createEvent(user, PROFILE_CHANGED, "updated profile."));

        AccountDto account = new AccountDto();
        account.setName(userDto.getAccountName());
        accountService.update(user, account);
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

    @Override
    public Map<Long, String> findUserNames(UserDetailsImpl user) {
        return userDao.findUserNames(user.getUser().getAccountId());
    }
}
