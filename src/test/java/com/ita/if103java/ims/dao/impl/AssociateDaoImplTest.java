package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.entity.Associate;
import com.ita.if103java.ims.entity.AssociateType;
import com.ita.if103java.ims.mapper.jdbc.EventRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class AssociateDaoImplTest {
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

    @InjectMocks
    private AssociateDaoImpl associateDao;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    private Associate associate;

    @BeforeEach
    void setUp() {
        associate = new Associate();
        associate.setAccountId(1L);
        associate.setName("Associate name");
        associate.setEmail("test@test.com");
        associate.setPhone("+380977959707");
        associate.setAdditionalInfo("additionalInfo");
        associate.setType(AssociateType.SUPPLIER);
        associate.setActive(true);

        when(this.keyHolder.getKey()).thenReturn(1L);
        when(this.jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenReturn(1);
        when(this.jdbcTemplate.update(anyString(), anyLong())).thenReturn(1);
        when(this.generatedKeyHolderFactory.newKeyHolder()).thenReturn(keyHolder);
        when(this.dataSource.getConnection()).thenReturn(this.connection);
        when(this.generatedKeyHolderFactory.newKeyHolder()).thenReturn(keyHolder);
        when(this.connection.prepareStatement(anyString(), anyInt())).thenReturn(this.preparedStatement);
        when(jdbcTemplate.query(stringArgumentCaptor.capture(),
            ArgumentMatchers.<EventRowMapper>any())).thenReturn(new ArrayList<Associate>());
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(5);

    }

    @Test
    void testCreate_successFlow() {
        assertEquals(associate, associateDao.create(associate.getAccountId(), associate));
        assertNotNull(associate.getId());
    }

    @Test
    void findById() {
    }

    @Test
    void findByAccountId() {
    }

    @Test
    void getAssociates() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    @Test
    void getAssociatesByType() {
    }
}
