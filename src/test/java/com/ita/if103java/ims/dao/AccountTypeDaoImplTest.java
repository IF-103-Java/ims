package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.dao.impl.AccountTypeDaoImpl;
import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.exception.dao.AccountTypeNotFoundException;
import com.ita.if103java.ims.exception.dao.CRUDException;
import com.ita.if103java.ims.mapper.jdbc.AccountTypeRowMapper;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class AccountTypeDaoImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private DataSource dataSource;

    @InjectMocks
    private AccountTypeDaoImpl accountTypeDao;

    private AccountType accountType;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(this.jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(this.jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenReturn(1);

        accountType = new AccountType(2L, "Premium", 300.0, 2,
            100, 100, 100, 100, 100,
            true, true, true);
    }

    @Test
    void findById_successFlow() {
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<AccountTypeRowMapper>any(), anyLong()))
            .thenReturn(accountType);

        assertEquals(accountType, accountTypeDao.findById(accountType.getId()));

        verify(jdbcTemplate).queryForObject(anyString(), ArgumentMatchers.<AccountTypeRowMapper>any(), eq(accountType.getId()));
    }

    @Test
    void findById_AccountTypeNotFoundException() {
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<AccountTypeRowMapper>any(), anyLong()))
            .thenThrow(new AccountTypeNotFoundException());

        assertThrows(AccountTypeNotFoundException.class, () -> accountTypeDao.findById(accountType.getId()));

        verify(jdbcTemplate).queryForObject(anyString(), ArgumentMatchers.<AccountTypeRowMapper>any(), eq(accountType.getId()));
    }

    @Test
    void findById_CRUDException() {
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<AccountTypeRowMapper>any(), anyLong()))
            .thenThrow(new CRUDException());

        assertThrows(CRUDException.class, () -> accountTypeDao.findById(accountType.getId()));

        verify(jdbcTemplate).queryForObject(anyString(), ArgumentMatchers.<AccountTypeRowMapper>any(), eq(accountType.getId()));
    }

    @Test
    void selectAllPossibleToUpgrade_successFlow() {
        List<AccountType> allPossible = Arrays.asList(
            new AccountType(1L, "Basic", 0.0, 1, 3, 3,
                3, 3, 3, false,
                false, true), accountType);

        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<AccountTypeRowMapper>any(), eq(accountType.getLevel()))).thenReturn(allPossible);

        assertEquals(allPossible, accountTypeDao.selectAllPossibleToUpgrade(accountType.getLevel()));

        verify(jdbcTemplate).query(anyString(), ArgumentMatchers.<AccountTypeRowMapper>any(), eq(accountType.getLevel()));
    }

    @Test
    void minLvlType_successFlow() {
        Long accountTypeWithMinLvlId = 1L;

        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class))).thenReturn(accountTypeWithMinLvlId);

        assertEquals(accountTypeWithMinLvlId, accountTypeDao.minLvlType());

        verify(jdbcTemplate).queryForObject(anyString(), eq(Long.class));
    }
}
