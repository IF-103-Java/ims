package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.config.GeneratedKeyHolderFactory;
import com.ita.if103java.ims.dao.impl.AssociateDaoImpl;
import com.ita.if103java.ims.entity.Associate;
import com.ita.if103java.ims.entity.AssociateType;
import com.ita.if103java.ims.exception.dao.AssociateEntityNotFoundException;
import com.ita.if103java.ims.exception.dao.CRUDException;
import com.ita.if103java.ims.mapper.jdbc.AssociateRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

    private Associate associate;
    private List<Associate> associateList;
    private PageRequest pageable;
    private Long accountId = 2L;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.initMocks(this);

        associate = new Associate();
        associate.setAccountId(1L);
        associate.setName("Associate name");
        associate.setEmail("test@test.com");
        associate.setPhone("+380977959707");
        associate.setAdditionalInfo("additionalInfo");
        associate.setType(AssociateType.SUPPLIER);
        associate.setActive(true);

        associateList = Collections.singletonList(associate);
        pageable = PageRequest.of(0, 15, Sort.Direction.ASC, "id");

        when(this.jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(this.dataSource.getConnection()).thenReturn(this.connection);
        when(this.connection.prepareStatement(anyString(), anyInt())).thenReturn(this.preparedStatement);
        when(this.generatedKeyHolderFactory.newKeyHolder()).thenReturn(keyHolder);
        when(this.keyHolder.getKey()).thenReturn(1L);
        when(this.jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenReturn(1);
    }

    @Test
    void testCreate_successFlow() {
        assertEquals(associate, associateDao.create(accountId, associate));
        assertNotNull(associate.getId());
    }

    @Test
    void testCreate_omittedNotNullFields() {
        when(this.keyHolder.getKey()).thenReturn(null);

        // Creating empty user item
        Associate associate = new Associate();
        assertThrows(CRUDException.class, () -> associateDao.create(accountId, associate));
    }


    @Test
    void testFindById_successFlow() {
        Long id = 1L;
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<AssociateRowMapper>any(), anyLong(), anyLong()))
            .thenReturn(associate);

        assertEquals(associate, associateDao.findById(accountId, id));
    }

    @Test
    void testFindByAccountId_successFlow() {
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<AssociateRowMapper>any(), eq(accountId)))
            .thenReturn(associateList);

        assertEquals(associateList, associateDao.findByAccountId(accountId));
    }

    @Test
    void testGetAssociates_successFlow() {
        Page<Associate> associatePage = new PageImpl<Associate>(associateList, pageable, accountId);

        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<AssociateRowMapper>any(),
            eq(accountId), eq(pageable.getPageSize()), eq(pageable.getOffset()))).thenReturn(associateList);
        when(jdbcTemplate.queryForObject(anyString(), any(), eq(Integer.class))).thenReturn(associateList.size());

        Page<Associate> result = associateDao.getAssociates(pageable, accountId);

        assertEquals(associatePage, result);
    }

    @Test
    void testUpdate_successFlow() {
        when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(2);

        assertEquals(associate, associateDao.update(accountId, associate));
    }

    @Test
    void testUpdate_failFlowAssociateEntityNotFoundException() {
        when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(0);
        //should throw AssociateEntityNotFoundException if user doesn't exist
        assertThrows(AssociateEntityNotFoundException.class,
            () -> associateDao.update(accountId, associate));
    }

    @Test
    void testDelete_successFlow() {
        when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(1);

        assertTrue(associateDao.delete(accountId, associate.getId()));
    }

    @Test
    void testDelete_failFlowAssociateEntityNotFoundException() {
        when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(0);

        assertThrows(AssociateEntityNotFoundException.class,
            () -> associateDao.update(accountId, associate));
    }

    @Test
    void testHardDelete_successFlow() {
        when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(1);

        assertTrue(associateDao.hardDelete(accountId));
    }

    @Test
    void getAssociatesType_successFlow() {
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<AssociateRowMapper>any(), anyLong(), anyString()))
            .thenReturn(associateList);
        assertEquals(associateList, associateDao.getAssociatesByType(accountId, AssociateType.SUPPLIER));
    }

    @Test
    void getAssociatesType_omittedFlow() {
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<AssociateRowMapper>any(), anyLong(), anyString()))
            .thenThrow(new DataAccessException("") {
            });
        assertThrows(CRUDException.class, () -> associateDao.getAssociatesByType(accountId, AssociateType.SUPPLIER));
    }
}
