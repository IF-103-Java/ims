package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.config.GeneratedKeyHolderFactory;
import com.ita.if103java.ims.dao.impl.EventDaoImpl;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.exception.dao.CRUDException;
import com.ita.if103java.ims.mapper.jdbc.EventRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventDaoImplTest {

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
    private EventDaoImpl eventDao;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    private User user;
    private Event event;
    private ZonedDateTime currentDateTime = ZonedDateTime.now();
    private Map<String, Object> params;
    private PageRequest pageable;


    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.initMocks(this);
        event = new Event();
        event.setDate(currentDateTime);
        event.setName(EventName.NEW_CLIENT);
        event.setAuthorId(4l);
        event.setMessage("New client test");
        event.setAccountId(2l);

        user = new User();
        user.setRole(Role.ROLE_ADMIN);
        user.setAccountId(2l);
        user.setId(4l);

        params = new HashMap<>();
        params.put("type", new ArrayList<>(Arrays.asList("USER", "WAREHOUSE")));
        params.put("after", "02-02-2002");

        pageable = PageRequest.of(0, 15, Sort.Direction.ASC, "id");

        when(this.keyHolder.getKey()).thenReturn(1L);
        when(this.jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenReturn(1);
        when(this.jdbcTemplate.update(anyString(), anyLong())).thenReturn(1);
        when(this.generatedKeyHolderFactory.newKeyHolder()).thenReturn(keyHolder);
        when(this.dataSource.getConnection()).thenReturn(this.connection);
        when(this.generatedKeyHolderFactory.newKeyHolder()).thenReturn(keyHolder);
        when(this.connection.prepareStatement(anyString(), anyInt())).thenReturn(this.preparedStatement);
        when(jdbcTemplate.query(stringArgumentCaptor.capture(),
            ArgumentMatchers.<EventRowMapper>any())).thenReturn(new ArrayList<Event>());
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(5);
    }

    @Test
    public void testCreate_successFlow() {
        assertEquals(event, eventDao.create(event));
        assertNotNull(user.getId());
    }

    @Test
    void testCreate_omittedNotNullFields() {
        when(this.keyHolder.getKey()).thenReturn(null);
        event = new Event();
        assertThrows(CRUDException.class, () -> eventDao.create(event));
    }

    @Test
    public void testDeleteByAccountId() {
        eventDao.deleteByAccountId(user.getAccountId());
        verify(jdbcTemplate, times(1)).update(anyString(), eq(user.getAccountId()));
    }

    @ParameterizedTest
    @ArgumentsSource(FindAllArgumentsProvider.class)
    public void testFindAll_successFlow(Role role, Map<String, Object> params, String expectedQuery) {
        user.setRole(role);
        eventDao.findAll(pageable, params, user);
        assertEquals(expectedQuery,
            stringArgumentCaptor.getValue().replaceAll("\\s{2,}", " ").trim());
    }

    static class FindAllArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {

            HashMap<String, Object> params1 = new HashMap<>();
            params1.put("type", new ArrayList<>(Arrays.asList("USER", "WAREHOUSE")));
            params1.put("after", "02-02-2002");
            String expectedQuery1 = """
                SELECT * FROM events WHERE account_id = '2' and DATE(date) >= '02-02-2002' and (name in
                ('ITEM_ENDED', 'LOW_SPACE_IN_WAREHOUSE', 'WAREHOUSE_CREATED', 'WAREHOUSE_EDITED',
                'WAREHOUSE_REMOVED') or (name in ('LOGIN', 'LOGOUT', 'PASSWORD_CHANGED', 'PROFILE_CHANGED', 'SIGN_UP')
                and author_id = '4')) ORDER BY id ASC LIMIT 15 OFFSET 0
                """.replace("\n", " ").replaceAll("\\s{2,}", " ").trim();

            HashMap<String, Object> params2 = new HashMap<>();
            params2.put("type", new ArrayList<>(Arrays.asList("USER", "WAREHOUSE", "TRANSACTION")));
            params2.put("before", "02-02-2002");
            String expectedQuery2 = """
                SELECT * FROM events WHERE account_id = '2' and DATE(date) <= '02-02-2002' and
                name in ('ITEM_CAME', 'ITEM_ENDED', 'ITEM_MOVED', 'ITEM_SHIPPED', 'LOGIN', 'LOGOUT',
                'LOW_SPACE_IN_WAREHOUSE', 'PASSWORD_CHANGED', 'PROFILE_CHANGED', 'SIGN_UP', 'WAREHOUSE_CREATED',
                'WAREHOUSE_EDITED', 'WAREHOUSE_REMOVED') ORDER BY id ASC LIMIT 15 OFFSET 0
                """.replace("\n", " ").replaceAll("\\s{2,}", " ").trim();

            HashMap<String, Object> params3 = new HashMap<>();
            params3.put("name", Arrays.asList("NEW_CLIENT", "WAREHOUSE_EDITED", "PASSWORD_CHANGED"));
            params3.put("author_id", Arrays.asList(2, 4, 5, 8, 13));
            String expectedQuery3 = """
                SELECT * FROM events WHERE account_id = '2' and author_id in ('2', '4', '5', '8', '13') and
                (name in ('NEW_CLIENT', 'WAREHOUSE_EDITED') or (name in ('PASSWORD_CHANGED') and author_id = '4'))
                ORDER BY id ASC LIMIT 15 OFFSET 0
                """.replace("\n", " ").replaceAll("\\s{2,}", " ").trim();

            HashMap<String, Object> params4 = new HashMap<>();
            params4.put("type", Arrays.asList("USER"));
            params4.put("after", "02-02-2002");
            String expectedQuery4 = """
                SELECT * FROM events WHERE account_id = '2' and DATE(date) >= '02-02-2002' and
                (name in ('LOGIN', 'LOGOUT', 'PASSWORD_CHANGED', 'PROFILE_CHANGED', 'SIGN_UP') and
                author_id = '4') ORDER BY id ASC LIMIT 15 OFFSET 0
                """.replace("\n", " ").replaceAll("\\s{2,}", " ").trim();

            HashMap<String, Object> params5 = new HashMap<>();
            params5.put("type", Arrays.asList("USER"));
            params5.put("author_id", Arrays.asList(4));
            String expectedQuery5 = """
                SELECT * FROM events WHERE account_id = '2' and author_id in ('4') and
                (name in ('LOGIN', 'LOGOUT', 'PASSWORD_CHANGED', 'PROFILE_CHANGED', 'SIGN_UP') and
                author_id = '4') ORDER BY id ASC LIMIT 15 OFFSET 0
                """.replace("\n", " ").replaceAll("\\s{2,}", " ").trim();

            HashMap<String, Object> params6 = new HashMap<>();
            params6.put("type", Arrays.asList("USER", "TRANSACTION"));
            params6.put("author_id", Arrays.asList(2, 5, 6));
            String expectedQuery6 = """
                SELECT * FROM events WHERE account_id = '2' and author_id in ('2', '5', '6') and
                name in ('ITEM_CAME', 'ITEM_MOVED', 'ITEM_SHIPPED') ORDER BY id ASC LIMIT 15 OFFSET 0
                """.replace("\n", " ").replaceAll("\\s{2,}", " ").trim();

            HashMap<String, Object> params7 = new HashMap<>();
            params7.put("name", Arrays.asList("NEW_CLIENT", "LOGOUT"));
            params7.put("type", Arrays.asList("TRANSACTION"));
            params7.put("author_id", Arrays.asList(2, 4, 5, 8, 13));
            String expectedQuery7 = """
                SELECT * FROM events WHERE account_id = '2' and author_id in ('2', '4', '5', '8', '13') and
                (name in ('ITEM_CAME', 'ITEM_MOVED', 'ITEM_SHIPPED', 'NEW_CLIENT') or
                (name in ('LOGOUT') and author_id = '4')) ORDER BY id ASC LIMIT 15 OFFSET 0
                """.replace("\n", " ").replaceAll("\\s{2,}", " ").trim();


            HashMap<String, Object> params8 = new HashMap<>();
            params8.put("author_id", Arrays.asList(2, 5, 8, 13));
            String expectedQuery8 = """
                SELECT * FROM events WHERE account_id = '2' and author_id in ('2', '5', '8', '13') and
                (name not in ('LOGIN', 'LOGOUT', 'PASSWORD_CHANGED', 'PROFILE_CHANGED', 'SIGN_UP'))
                ORDER BY id ASC LIMIT 15 OFFSET 0
                """.replace("\n", " ").replaceAll("\\s{2,}", " ").trim();

            HashMap<String, Object> params9 = new HashMap<>();
            params9.put("type", Arrays.asList("USER"));
            params9.put("author_id", Arrays.asList(3));
            String expectedQuery9 = """
                SELECT * FROM events WHERE account_id = '2' and author_id in ('3') and
                name in ('') ORDER BY id ASC LIMIT 15 OFFSET 0
                """.replace("\n", " ").replaceAll("\\s{2,}", " ").trim();


            return Stream.of(
                Arguments.of(Role.ROLE_WORKER, params1, expectedQuery1),
                Arguments.of(Role.ROLE_ADMIN, params2, expectedQuery2),
                Arguments.of(Role.ROLE_WORKER, params3, expectedQuery3),
                Arguments.of(Role.ROLE_WORKER, params4, expectedQuery4),
                Arguments.of(Role.ROLE_WORKER, params5, expectedQuery5),
                Arguments.of(Role.ROLE_WORKER, params6, expectedQuery6),
                Arguments.of(Role.ROLE_WORKER, params7, expectedQuery7),
                Arguments.of(Role.ROLE_WORKER, params8, expectedQuery8),
                Arguments.of(Role.ROLE_WORKER, params9, expectedQuery9)
            );
        }
    }

}
