package com.ita.if103java.ims.service;

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
import com.ita.if103java.ims.dto.ResetPasswordDto;
import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.Account;
import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.mapper.dto.AccountDtoMapper;
import com.ita.if103java.ims.mapper.dto.UserDtoMapper;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ita.if103java.ims.util.DataUtil.getListOfUsers;
import static com.ita.if103java.ims.util.DataUtil.getTestUser;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceImplTest {
    @Mock
    private UserDao userDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EventService eventService;
    @Mock
    private AccountService accountService;
    @Mock
    private AccountDao accountDao;
    @Mock
    private AccountTypeDao accountTypeDao;
    @Mock
    private EventDao eventDao;
    @Mock
    private TransactionDao transactionDao;
    @Mock
    private AddressDao addressDao;
    @Mock
    private AssociateDao associateDao;
    @Mock
    private SavedItemDao savedItemDao;
    @Mock
    private ItemDao itemDao;
    @Mock
    private WarehouseDao warehouseDao;
    @Mock
    private MailService mailService;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDtoMapper mapper;
    private AccountDtoMapper accountMapper;
    private UserDto userDto;
    private UserDto createdUserDto;
    private User user;
    private ZonedDateTime currentDateTime;
    private Account account;
    private AccountType accountType;
    private ResetPasswordDto resetPasswordDto;
    private String newPassword;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mapper = new UserDtoMapper();
        accountMapper = new AccountDtoMapper();
        currentDateTime = ZonedDateTime.now(ZoneId.systemDefault());
        userService = new UserServiceImpl(
            userDao,
            mapper,
            passwordEncoder,
            eventService,
            accountService,
            mailService,
            accountDao,
            accountTypeDao,
            eventDao,
            transactionDao,
            addressDao,
            associateDao,
            savedItemDao,
            itemDao,
            warehouseDao
        );

        when(this.passwordEncoder.encode(anyString()))
            .thenReturn("$2a$10$jl.3oXcrjxo9qcCUoQIzT.TKxCNHAvlDaL3uh/ekUM.XpSa/Rhnse");

        //Initializing test user
        user = getTestUser();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDto = mapper.toDto(user);

        //Initializing created user
        createdUserDto = new UserDto();
        createdUserDto.setId(1l);
        createdUserDto.setFirstName("Mary");
        createdUserDto.setLastName("Smith");
        createdUserDto.setEmail("mary.smith@gmail.com");
        createdUserDto.setPassword("qwerty123");
        createdUserDto.setAccountName("Factory");


        //Initializing resetPasswordDto
        newPassword = "12345678";
        resetPasswordDto = new ResetPasswordDto();
        resetPasswordDto.setCurrentPassword(user.getPassword());
        resetPasswordDto.setNewPassword(newPassword);


        //Initializing account and accountType
        accountType = new AccountType();
        accountType.setMaxUsers(3);
        account = new Account();
        account.setId(1l);
        account.setTypeId(1l);
        when(accountDao.findById(userDto.getAccountId())).thenReturn(account);
        when(accountTypeDao.findById(account.getTypeId())).thenReturn(accountType);

        when(userDao.findById(user.getId())).thenReturn(user);
        when(userDao.findByEmailUUID(userDto.getEmailUUID())).thenReturn(user);


    }

    @Test
    void create_successFlow() {
        //Preparing user for creating
        user.setActive(false);
        user.setAccountId(null);

        when(userDao.create(any(User.class))).thenReturn(user);
        UserDto resultUserDto = userService.create(createdUserDto);

        // Checking if the id was generated
        assertNotNull(resultUserDto.getId());

        // Checking if the user isn't active
        assertFalse(resultUserDto.isActive());

        //Checking if accountId is null
        assertNull(resultUserDto.getAccountId());

        //Checking if role is Admin
        assertEquals(Role.ROLE_ADMIN, resultUserDto.getRole());
    }

    @Test
    void createAndSendMessage_successFlow() {
        when(userDao.create(any(User.class))).thenReturn(user);
        when(accountService.create(any(UserDto.class), anyString()))
            .thenReturn(accountMapper.toDto(account));

        UserDto resultUser = userService.createAndSendMessage(createdUserDto);
        //Checking if the correct user was returned
        assertEquals(userDto, resultUser);

        //Checking if accountId was set
        assertNotNull(resultUser.getAccountId());

        //Verifying if method was called
        verify(mailService).sendMessage(any(UserDto.class), anyString(), anyString());
    }

    @Test
    void findById_successFlow() {
        UserDto resultUserDto = userService.findById(user.getId());

        // Checking if the correct user was returned except of password
        assertEquals(userDto, resultUserDto);
    }

    @Test
    void findAdminByAccountId_successFlow() {
        when(userDao.findAdminByAccountId(userDto.getAccountId())).thenReturn(user);
        UserDto resultUserDto = userService.findAdminByAccountId(user.getAccountId());

        // Checking if the correct user was returned except of password
        assertEquals(userDto, resultUserDto);
    }

    @Test
    void findByEmail_successFlow() {
        when(userDao.findByEmail(user.getEmail())).thenReturn(user);
        UserDto resultUserDto = userService.findByEmail(user.getEmail());

        // Checking if the correct user was returned except of password
        assertEquals(userDto, resultUserDto);
    }

    @Test
    void findAll_successFlow() {
        // Initializing users list
        List<User> users = getListOfUsers();

        //Expected users list
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(users.get(1));

        PageRequest pageable = PageRequest.of(0, 3, Sort.Direction.ASC, "id");
        when(userDao.findAll(pageable, userDto.getAccountId())).thenReturn(expectedUsers);
        when(userDao.countOfUsers(userDto.getAccountId())).thenReturn(expectedUsers.size());

        // First 3 users (should return 1, because there is only 1 user with role WORKER and an active status in db)
        Page<UserDto> resultUserList = userService.findAll(pageable, userDto.getAccountId());
        assertEquals(expectedUsers.size(), resultUserList.getContent().size());

        assertEquals(expectedUsers, mapper.toEntityList(resultUserList.getContent()));

    }

    @Test
    void update_successFlow() {
        user.setFirstName("MaryNew");
        user.setLastName("SmithNew");
        userDto = mapper.toDto(user);

        when(userDao.update(user)).thenReturn(user);

        UserDto resultUserDto = userService.update(userDto);
        // Checking if the correct user was returned
        assertEquals(userDto, resultUserDto);

        verify(eventService).create(any(Event.class));

    }

    @Test
    void hardDelete_successFlow() {
        Long accountId = userDto.getAccountId();
        Long userId = userDto.getId();
        when(userDao.findById(user.getId())).thenReturn(user);

        if (userDto.getRole() == Role.ROLE_WORKER) {
            when(userDao.activate(userId, accountId, false)).thenReturn(true);
        } else {
            when(accountDao.hardDelete(accountId)).thenReturn(true);
        }
        //Should be called a hard delete method according to a user role
        assertTrue(userService.delete(userId, accountId));

        if (userDto.getRole() == Role.ROLE_ADMIN) {
            verify(eventDao).deleteByAccountId(accountId);
            verify(transactionDao).hardDelete(accountId);
            verify(addressDao).hardDelete(accountId);
            verify(associateDao).hardDelete(accountId);
            verify(savedItemDao).hardDelete(accountId);
            verify(itemDao).hardDelete(accountId);
            verify(warehouseDao).hardDelete(accountId);
            verify(userDao).hardDelete(accountId);
            verify(accountDao).hardDelete(accountId);
        }
    }


    @Test
    void updatePassword_illegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> userService.updatePassword(userDto, resetPasswordDto));
    }

    @Test
    void updatePassword_successFlow() {
        String newPassword = "12345678";

        ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        resetPasswordDto.setCurrentPassword(userDto.getPassword());
        resetPasswordDto.setNewPassword(newPassword);

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertTrue(userService.updatePassword(userDto, resetPasswordDto));

        verify(userDao).updatePassword(userDto.getId(), userDto.getPassword());
        verify(eventService).create(any(Event.class));
    }

    @Test
    void activateUser_successFlow() {
        //Should return 2 to activate next user
        when(userDao.countOfUsers(userDto.getAccountId())).thenReturn(2);

        when(userDao.activate(userDto.getId(), userDto.getAccountId(), true)).thenReturn(true);
        when(accountDao.activate(userDto.getAccountId())).thenReturn(true);

        // Checking if true was returned
        assertTrue(userService.activateUser(userDto.getEmailUUID()));

        // Checking if the user is active
        assertTrue(user.isActive());

        // Checking if userDao.update(User user) method was called
        verify(userDao).activate(userDto.getId(), userDto.getAccountId(), true);
    }

    @Test
    void activateUser_badFlow() {
        //Should return 3 to hard delete next user
        when(userDao.countOfUsers(userDto.getAccountId())).thenReturn(3);

        // Checking if true was returned
        assertFalse(userService.activateUser(userDto.getEmailUUID()));

        // Checking if hard delete method was called
        verify(userDao).hardDelete(userDto.getAccountId());
    }

    @Test
    void findAllUserNames_successFlow() {
        Map<Long, String> usernames = new HashMap();
        usernames.put(1l, "Mary Smith");
        usernames.put(2l, "Lucy Clack");

        when(userDao.findAllUserNames(userDto.getAccountId())).thenReturn(usernames);
        assertEquals(usernames, userService.findAllUserNames(new UserDetailsImpl(user)));
    }


    @Test
    void isAllowedToInvite_successFlow() {
        //should return true according to count of users is less then allowed
        assertTrue(userService.isAllowedToInvite(userDto.getAccountId()));
    }
}
