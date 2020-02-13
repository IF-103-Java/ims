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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class UserDaoImplTest {
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private PasswordEncoder passwordEncoder;
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

    @InjectMocks
    private UserDaoImpl userDao;

    private User user;


    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.initMocks(this);
        when(this.jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(this.dataSource.getConnection()).thenReturn(this.connection);
        when(this.connection.prepareStatement(anyString(), anyInt())).thenReturn(this.preparedStatement);
        when(this.generatedKeyHolderFactory.newKeyHolder()).thenReturn(keyHolder);
        when(this.keyHolder.getKey()).thenReturn(1L);
        when(this.jdbcTemplate.update(Mockito.any(PreparedStatementCreator.class), Mockito.any(KeyHolder.class))).thenReturn(1);


        // Initializing test user
        ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.systemDefault());
        user = new User();
        user.setFirstName("Mary");
        user.setLastName("Smith");
        user.setEmail("mary.smith@gmail.com");
        user.setPassword(passwordEncoder.encode("qwerty12345"));
        user.setRole(Role.ROLE_ADMIN);
        user.setCreatedDate(currentDateTime);
        user.setUpdatedDate(currentDateTime);
        user.setActive(false);
        user.setEmailUUID(UUID.randomUUID().toString());
        user.setAccountId(1l);

        userDao.create(user);
    }

    @Test
    void testCreate_successFlow() {
        // Should return reference to the same object
        assertEquals(user, userDao.create(user));
        // Checking if the id was generated
        assertNotNull(user.getId());
        // Checking if the user isn't active
        assertFalse(user.isActive());
    }

    @Test
    void testCreate_omittedNotNullFields() {
        when(this.keyHolder.getKey()).thenReturn(null);

        // Creating empty user item
        User user = new User();
        assertThrows(CRUDException.class, () -> userDao.create(user));
    }

    @Test
    void testFindById() {
        System.out.println(user.getId());
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<UserRowMapper>any(), anyLong()))
            .thenReturn(user);

        assertEquals(user, userDao.findById(user.getId()));
    }

    @Test
    void testFindAdminByAccountId() {
        when(this.jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<UserRowMapper>any(), anyLong()))
            .thenReturn(user);

        assertEquals(user, userDao.findAdminByAccountId(user.getAccountId()));
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
        assertTrue(userDao.hardDelete(user.getAccountId()));
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
