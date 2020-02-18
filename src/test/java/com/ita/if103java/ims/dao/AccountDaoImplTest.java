package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.config.GeneratedKeyHolderFactory;
import com.ita.if103java.ims.dao.impl.AccountDaoImpl;
import com.ita.if103java.ims.dao.impl.AccountTypeDaoImpl;
import com.ita.if103java.ims.entity.Account;
import com.ita.if103java.ims.exception.dao.AccountNotFoundException;
import com.ita.if103java.ims.exception.dao.CRUDException;
import com.ita.if103java.ims.mapper.jdbc.AccountRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
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
    private AccountTypeDaoImpl accountTypeDao;

    @InjectMocks
    private AccountDaoImpl accountDao;

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

        account = new Account(1L, "Account name", 1L, currentDateTime, true);
    }

    @Test
    public void create_successFlow() {
        when(accountTypeDao.minLvlType()).thenReturn(1L);

        assertEquals(account, accountDao.create(account));

        verify(accountTypeDao).minLvlType();
    }

    @Test
    public void create_failFlow() {
        when(accountTypeDao.minLvlType()).thenReturn(1L);
        when(this.keyHolder.getKey()).thenReturn(null);

        assertThrows(CRUDException.class, () -> accountDao.create(new Account()));

        verify(accountTypeDao).minLvlType();
    }

    @Test
    public void findById_successFlow() {

        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<AccountRowMapper>any(), anyLong()))
            .thenReturn(account);

        assertEquals(account, accountDao.findById(account.getId()));

        verify(jdbcTemplate).queryForObject(anyString(), ArgumentMatchers.<AccountRowMapper>any(), eq(account.getId()));
    }

    @Test
    public void update_successFlow() {

        when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(1);

        assertEquals(account, accountDao.update(account));

        verify(jdbcTemplate).update(anyString(), eq(account.getName()), eq(account.getId()));
    }

    @Test
    public void update_AccountNotFoundException() {

        when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(0);

        assertThrows(AccountNotFoundException.class, () -> accountDao.update(account));

        verify(jdbcTemplate).update(anyString(), eq(account.getName()), eq(account.getId()));
    }

    @Test
    public void activate_successFlow() {
        when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(1);

        accountDao.activate(account.getId());

        verify(jdbcTemplate).update(anyString(), eq(true), eq(account.getId()));
    }

    @Test
    public void activate_AccountNotFoundException() {
        when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(0);

        assertThrows(AccountNotFoundException.class, () -> accountDao.activate(account.getId()));

        verify(jdbcTemplate).update(anyString(), eq(true), eq(account.getId()));
    }

    @Test
    public void delete_successFlow() {
        when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(1);

        accountDao.delete(account.getId());

        verify(jdbcTemplate).update(anyString(), eq(false), anyLong());
    }

    @Test
    public void delete_AccountNotFoundException() {
        when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(0);

        assertThrows(AccountNotFoundException.class, () -> accountDao.delete(account.getId()));

        verify(jdbcTemplate).update(anyString(), eq(false), anyLong());
    }

    @Test
    public void upgradeAccount_successFlow() {
        when(jdbcTemplate.update(anyString(), anyLong(), anyLong())).thenReturn(1);

        assertTrue(accountDao.upgradeAccount(account.getId(), account.getTypeId()));

        verify(jdbcTemplate).update(anyString(), eq(account.getId()), eq(account.getTypeId()));
    }

    @Test
    public void upgradeAccount_AccountNotFoundException() {
        when(jdbcTemplate.update(anyString(), anyLong(), anyLong())).thenReturn(0);

        assertThrows(AccountNotFoundException.class, () -> accountDao.upgradeAccount(account.getId(), account.getTypeId()));

        verify(jdbcTemplate).update(anyString(), eq(account.getId()), eq(account.getTypeId()));
    }

    @Test
    public void hardDelete_successFlow() {
        when(jdbcTemplate.update(anyString(), anyLong())).thenReturn(1);

        assertTrue(accountDao.hardDelete(account.getId()));

        verify(jdbcTemplate).update(anyString(), anyLong());
    }

    @Test
    public void hardDelete_AccountNotFoundException() {
        when(jdbcTemplate.update(anyString(), anyLong())).thenReturn(1);

        assertTrue(accountDao.hardDelete(account.getId()));

        verify(jdbcTemplate).update(anyString(), anyLong());
    }
}
