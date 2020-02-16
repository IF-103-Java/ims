package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.config.GeneratedKeyHolderFactory;
import com.ita.if103java.ims.entity.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class AccountDaoImplTest {

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
    private AccountDaoImplTest accountDao;

    private Account account;
    private ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.systemDefault());

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.initMocks(this);
        when(this.jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(this.dataSource.getConnection()).thenReturn(this.connection);
        when(this.connection.prepareStatement(anyString(), anyInt())).thenReturn(this.preparedStatement);
        when(this.generatedKeyHolderFactory.newKeyHolder()).thenReturn(keyHolder);
        when(this.keyHolder.getKey()).thenReturn(1L);
        when(this.jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenReturn(1);
        when(this.passwordEncoder.encode(anyString())).thenReturn("$2a$10$NXNtx47QhcfV1Uxf5lGYK.JJcIqbjgaQpSHlVfCX31HsvglFzLgi6");

        account = new Account();
        account.setId(1L);
        account.setName("Account name");
        account.setCreatedDate(currentDateTime);
        account.setTypeId(1L);
        account.setActive(true);
    }

    @Test
    void create() {

    }

    @Test
    void findById() {
    }

    @Test
    void update() {
    }

    @Test
    void activate() {
    }

    @Test
    void delete() {
    }

    @Test
    void upgradeAccount() {
    }

    @Test
    void hardDelete() {
    }
}
