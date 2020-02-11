package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.dao.AccountTypeDao;
import com.ita.if103java.ims.dao.AddressDao;
import com.ita.if103java.ims.dao.AssociateDao;
import com.ita.if103java.ims.dao.EventDao;
import com.ita.if103java.ims.dao.ItemDao;
import com.ita.if103java.ims.dao.SavedItemDao;
import com.ita.if103java.ims.dao.TransactionDao;
import com.ita.if103java.ims.dao.UserDao;
import com.ita.if103java.ims.dao.WarehouseDao;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    private AccountDao accountDao;
    private AccountTypeDao accountTypeDao;

    private EventDao eventDao;
    private TransactionDao transactionDao;
    private AddressDao addressDao;
    private AssociateDao associateDao;
    private SavedItemDao savedItemDao;
    private ItemDao itemDao;
    private WarehouseDao warehouseDao;


    @Autowired
    public UserServiceImpl(UserDao userDao,
                           UserDtoMapper mapper,
                           PasswordEncoder passwordEncoder,
                           EventService eventService,
                           AccountService accountService,
                           MailServiceImpl mailService,
                           AccountDao accountDao,
                           AccountTypeDao accountTypeDao,
                           EventDao eventDao,
                           TransactionDao transactionDao,
                           AddressDao addressDao,
                           AssociateDao associateDao,
                           SavedItemDao savedItemDao,
                           ItemDao itemDao,
                           WarehouseDao warehouseDao) {

        this.userDao = userDao;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.eventService = eventService;
        this.accountService = accountService;
        this.mailService = mailService;
        this.accountDao = accountDao;
        this.accountTypeDao = accountTypeDao;

        this.eventDao = eventDao;
        this.transactionDao = transactionDao;
        this.addressDao = addressDao;
        this.associateDao = associateDao;
        this.savedItemDao = savedItemDao;
        this.itemDao = itemDao;
        this.warehouseDao = warehouseDao;
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
        AccountDto account = accountService.create(createdUser, userDto.getAccountName());
        createdUser.setAccountId(account.getId());
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
    public UserDto findAdminByAccountId(Long accountID) {
        return mapper.toDto(userDao.findAdminByAccountId(accountID));
    }

    @Override
    public UserDto update(UserDto userDto) {
        User user = userDao.findById(userDto.getId());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        User updatedUser = userDao.update(user);
        user.setUpdatedDate(updatedUser.getUpdatedDate());
        eventService.create(createEvent(user, PROFILE_CHANGED, "updated profile."));

        return mapper.toDto(user);
    }


    @Transactional
    @Override
    public boolean delete(Long id, Long accountId) {
        User user = userDao.findById(id);
        if (user.getRole() == Role.ROLE_ADMIN) {
            eventDao.deleteByAccountId(accountId);
            transactionDao.hardDelete(accountId);
            addressDao.hardDelete(accountId);
            associateDao.hardDelete(accountId);
            savedItemDao.hardDelete(accountId);
            itemDao.hardDelete(accountId);
            warehouseDao.hardDelete(accountId);
            accountDao.hardDelete(accountId);
            return userDao.hardDelete(accountId);
        }
        return userDao.activate(id, accountId, false);
    }

    @Override
    public Page<UserDto> findAll(Pageable pageable, Long accountId) {
        List<User> users = userDao.findAll(pageable, accountId);
        Integer rowCount = userDao.countOfUsers(accountId);
        return new PageImpl<>(mapper.toDtoList(users), pageable, rowCount);
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
        Long accountId = activatedUser.getAccountId();
        Long userId = activatedUser.getId();

        if (isValidToken(activatedUser) && isAllowedToInvite(accountId)) {
            userDao.activate(userId, accountId, true);
            accountDao.activate(activatedUser.getAccountId());
            return true;
        } else {
            userDao.hardDelete(accountId);
            return false;
        }
    }

    @Override
    public Map<Long, String> findAllUserNames(UserDetailsImpl user) {
        return userDao.findAllUserNames(user.getUser().getAccountId());
    }

    @Override
    public boolean isAllowedToInvite(Long accountId) {
        Integer usersCount = userDao.countOfUsers(accountId);
        Integer usersAllowed = accountTypeDao.findById(accountDao.findById(accountId).getTypeId()).getMaxUsers();
        return usersCount < usersAllowed;
    }
}
