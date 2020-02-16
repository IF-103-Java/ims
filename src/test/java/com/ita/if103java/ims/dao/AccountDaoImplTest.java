package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.config.GeneratedKeyHolderFactory;
import com.ita.if103java.ims.dao.impl.AccountDaoImpl;
import com.ita.if103java.ims.dao.impl.AccountTypeDaoImpl;
import com.ita.if103java.ims.entity.Account;
import com.ita.if103java.ims.exception.dao.CRUDException;
import com.ita.if103java.ims.mapper.jdbc.AccountRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

        account = new Account();
        account.setId(1L);
        account.setName("Account name");
        account.setCreatedDate(currentDateTime);
        account.setTypeId(1L);
        account.setActive(true);
    }

    @Test
    public void createSuccess() {
        when(accountTypeDao.minLvlType()).thenReturn(1L);

        assertEquals(account, accountDao.create(account));

        verify(accountTypeDao, times(1)).minLvlType();
        }

    @Test
    public void createFail() {
        when(accountTypeDao.minLvlType()).thenReturn(1L);
        when(this.keyHolder.getKey()).thenReturn(null);

        assertThrows(CRUDException.class, () -> accountDao.create(new Account()));

        verify(accountTypeDao, times(1)).minLvlType();
    }

    @Test
    public void findById() {
        Long id = account.getId();

        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<AccountRowMapper>any(), anyLong()))
            .thenReturn(account);

        accountDao.findById(id);
       // assertEquals(account, accountDao.findById(account.getId()));
    }

    @Test
    public void update() {

        when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(1);
        // assertEquals(updatedAccount, accountDao.update(updatedAccount));
        accountDao.update(account);
    }

    @Test
    public void activate() {
        when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(1);
        accountDao.activate(account.getId());

      //  verify(jdbcTemplate, times(1)).update(anyLong());
    }

    @Test
    public void delete() {
        when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(1);
        accountDao.delete(account.getId());

    //    verify(jdbcTemplate, times(1)).update(anyString(), anyLong());
    }

    @Test
    public void upgradeAccount() {
        when(jdbcTemplate.update(anyString(), anyLong(), anyLong())).thenReturn(1);
        accountDao.upgradeAccount(account.getId(), account.getTypeId());
        //assertTrue
    //    verify(jdbcTemplate, times(1)).update(anyString(), anyLong());
    }

    @Test
    public void hardDelete() {
        when(jdbcTemplate.update(anyString(), anyLong())).thenReturn(1);
        accountDao.hardDelete(account.getId());
        //assertTrue
     //   verify(jdbcTemplate, times(1)).update(anyString(), anyLong());
    }
}
