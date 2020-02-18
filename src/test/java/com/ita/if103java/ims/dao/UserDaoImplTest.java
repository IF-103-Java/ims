package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.config.GeneratedKeyHolderFactory;
import com.ita.if103java.ims.dao.impl.UserDaoImpl;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.exception.dao.CRUDException;
import com.ita.if103java.ims.exception.dao.UserNotFoundException;
import com.ita.if103java.ims.mapper.jdbc.UserRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.ita.if103java.ims.DataUtil.getListOfUsers;
import static com.ita.if103java.ims.DataUtil.getTestUser;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserDaoImplTest {
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private KeyHolder keyHolder;
    @Mock
    private Connection connection;
    @Mock
    private DataSource dataSource;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private GeneratedKeyHolderFactory generatedKeyHolderFactory;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserDaoImpl userDao;

    private User user;
    private ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.systemDefault());

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.initMocks(this);
        when(this.jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(this.dataSource.getConnection()).thenReturn(connection);
        when(this.connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
        when(this.generatedKeyHolderFactory.newKeyHolder()).thenReturn(keyHolder);
        when(this.keyHolder.getKey()).thenReturn(1L);
        when(this.jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenReturn(1);
        when(this.passwordEncoder.encode(anyString())).thenReturn("$2a$10$NXNtx47QhcfV1Uxf5lGYK.JJcIqbjgaQpSHlVfCX31HsvglFzLgi6");


        // Initializing test user
        user = getTestUser();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    @Test
    void testCreate_successFlow() {
        // Initializing created user
        User createdUser = getTestUser();
        createdUser.setActive(false);
        createdUser.setAccountId(null);

        //Encoding password
        createdUser.setPassword(passwordEncoder.encode(user.getPassword()));
        // Should return reference to the same object
        assertEquals(createdUser, userDao.create(createdUser));
        // Checking if the id was generated
        assertNotNull(createdUser.getId());
        // Checking if the user isn't active
        assertFalse(createdUser.isActive());
    }

    @Test
    void testCreate_omittedNotNullFields() {
        when(this.keyHolder.getKey()).thenReturn(null);

        // Creating empty user item
        User user = new User();
        assertThrows(CRUDException.class, () -> userDao.create(user));
    }

    @Test
    void testFindById_successFlow() {
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<UserRowMapper>any(), anyLong()))
            .thenReturn(user);

        assertEquals(user, userDao.findById(user.getId()));
    }

    @Test
    void testFindAdminByAccountId_successFlow() {
        when(this.jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<UserRowMapper>any(), anyLong()))
            .thenReturn(user);

        assertEquals(user, userDao.findAdminByAccountId(user.getAccountId()));
    }

    @Test
    void testFindAll_successFlow() {
        // Initializing users list
        List<User> users = getListOfUsers();
        for (User user : users) {
            // Adding these users to database
            userDao.create(user);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        //Expected users list
        List<User> newList = new ArrayList<>();
        newList.add(users.get(1));
        PageRequest pageable = PageRequest.of(0, 3, Sort.Direction.ASC, "id");
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<UserRowMapper>any(), anyLong(), anyInt(), anyLong()))
            .thenReturn(newList);

        // First 3 users (should return 1, because there is only 1 user with role worker and is active in db)
        Integer expectedCount = 1;
        List<User> resultUserList = userDao.findAll(pageable, user.getAccountId());
        assertEquals(expectedCount, resultUserList.size());
    }

    @Test
    void testUpdate_successFlow() {
        // Creating updated user
        User updatedUser = user;
        updatedUser.setFirstName("Mary1");
        updatedUser.setLastName("Smith1");

        when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(2);
        assertEquals(updatedUser, userDao.update(updatedUser));
    }

    @Test
    void testUpdate_UserNotFoundException() {
        // Creating updated user
        User updatedUser = user;
        updatedUser.setFirstName("Mary1");
        updatedUser.setLastName("Smith1");

        when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(0);
        //should throw UserNotFoundException if user doesn't exist
        assertThrows(UserNotFoundException.class, () -> userDao.update(updatedUser));
    }


    @Test
    void testUpdateAccountId_successFlow() {
        Long userId = user.getId();
        Long accountId = 1l;

        when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(1);
        assertTrue(userDao.updateAccountId(userId, accountId));
    }

    @Test
    void testActivate_successFlow() {
        when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(1);
        assertTrue(userDao.activate(user.getId(), user.getAccountId(), true));
    }

    @Test
    void testHardDelete_successFlow() {
        when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(1);
        userDao.hardDelete(user.getAccountId());
        verify(jdbcTemplate, times(1)).update(anyString(), eq(user.getAccountId()));
    }

    @Test
    void testUpdatePassword_successFlow() {
        String newPassword = passwordEncoder.encode("qwerty12345");

        when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(1);
        userDao.updatePassword(user.getId(), newPassword);
        verify(jdbcTemplate, times(1)).update(anyString(), ArgumentMatchers.<Object[]>any());
    }

    @Test
    void testUpdatePassword_userNotFoundException() {
        String newPassword = passwordEncoder.encode("qwerty12345");
        when(jdbcTemplate.update(anyString(), anyLong(), anyString(), any(Timestamp.class), eq(user.getId()))).thenReturn(1);
        assertThrows(UserNotFoundException.class, () -> userDao.updatePassword(100l, newPassword));
    }

    @Test
    void testFindByEmailUUID_successFlow() {
        when(this.jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<UserRowMapper>any(),
            eq(user.getEmailUUID())))
            .thenReturn(user);

        assertEquals(user, userDao.findByEmailUUID(user.getEmailUUID()));
    }

    @Test
    void testCountOfUsers_successFlow() {
        Integer count = 1;
        when(this.jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(user.getAccountId())))
            .thenReturn(count);

        assertEquals(count, userDao.countOfUsers(user.getAccountId()));
    }
}
