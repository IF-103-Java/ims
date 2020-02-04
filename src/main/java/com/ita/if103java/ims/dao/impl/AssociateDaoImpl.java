package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.dao.AssociateDao;
import com.ita.if103java.ims.entity.Associate;
import com.ita.if103java.ims.entity.AssociateType;
import com.ita.if103java.ims.exception.dao.AssociateEntityNotFoundException;
import com.ita.if103java.ims.exception.dao.CRUDException;
import com.ita.if103java.ims.mapper.jdbc.AssociateRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class AssociateDaoImpl implements AssociateDao {

    private static final Logger logger = LoggerFactory.getLogger(AssociateDaoImpl.class);
    private AssociateRowMapper associateRowMapper;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public AssociateDaoImpl(DataSource dataSource, AssociateRowMapper associateRowMapper) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.associateRowMapper = associateRowMapper;
    }

    @Override
    public Associate create(Long accountId, Associate associate) {
        try {
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> createStatement(accountId, associate, connection), holder);
            associate.setId(Optional.ofNullable(holder.getKey())
                .map(Number::longValue)
                .orElseThrow(() -> new CRUDException("Error during an associate creation: " +
                    "Autogenerated key is null")));
            return associate;
        } catch (DataAccessException e) {
            throw new CRUDException("Error during 'create' associate, id = " + associate.getId(), e);
        }
    }

    @Override
    public Associate findById(Long accountId, Long id) {
        try {
            return jdbcTemplate.queryForObject(Queries.SQL_SELECT_ASSOCIATE_BY_ID, associateRowMapper, accountId, id);
        } catch (EmptyResultDataAccessException e) {
            throw new AssociateEntityNotFoundException("Failed to obtain associate during `select`, id = " + id);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `select` associate, id = " + id, e);
        }
    }

    @Override
    public List<Associate> findByAccountId(Long accountId) {
        try {
            return jdbcTemplate.query(Queries.SQL_SELECT_ASSOCIATE_BY_ACCOUNT_ID, associateRowMapper, accountId);
        } catch (EmptyResultDataAccessException e) {
            throw new AssociateEntityNotFoundException("Failed to obtain associate during `select`, id = " + accountId, e);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `select` associate, id = " + accountId, e);
        }
    }

    @Override
    public Page<Associate> getAssociates(Pageable pageable, long accountId) {

        try {
            String sort = pageable.getSort().stream().map(
                x -> x.getProperty() + " " + x.getDirection().name()).collect(Collectors.joining(", "));

            List<Associate> associates = jdbcTemplate.query(String.format(Queries.SQL_SELECT_SORTED_ASSOICATES, sort), associateRowMapper,
                accountId, pageable.getPageSize(), pageable.getOffset());

            Integer rowCount =
                jdbcTemplate.queryForObject(Queries.SQL_ROW_COUNT, new Object[]{accountId}, Integer.class);

            return new PageImpl<>(associates, pageable, rowCount);
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `select * `", e);
        }
    }

    @Override
    public Associate update(Long accountId, Associate associate) {
        int status;
        try {
            status = jdbcTemplate.update(
                Queries.SQL_UPDATE_ASSOCIATE,
                associate.getName(),
                associate.getEmail(),
                associate.getPhone(),
                associate.getAdditionalInfo(),
                accountId,
                associate.getId());

        } catch (DataAccessException e) {
            throw new CRUDException("Error during `update` associate, id = " + associate.getId(), e);
        }
        if (status == 0) {
            throw new AssociateEntityNotFoundException("Failed to obtain associate during `update`, id = " + associate.getId());
        }

        return associate;
    }

    @Override
    public boolean delete(Long accountId, Long id) {
        int status;
        try {
            status = jdbcTemplate.update(Queries.SQL_SET_ACTIVE_STATUS_ASSOCIATE, false, accountId, id);

        } catch (DataAccessException e) {
            throw new CRUDException("Error during `delete` associate, id  = " + id, e);
        }
        if (status == 0) {
            throw new AssociateEntityNotFoundException("Failed to obtain associate during `delete`, id =" + id);
        }

        return true;
    }

    private PreparedStatement createStatement(Long accountId, Associate associate, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(AssociateDaoImpl.Queries.SQL_CREATE_ASSOCIATE,
            Statement.RETURN_GENERATED_KEYS);

        int i = 0;
        preparedStatement.setLong(++i, accountId);
        preparedStatement.setString(++i, associate.getName());
        preparedStatement.setString(++i, associate.getEmail());
        preparedStatement.setString(++i, associate.getPhone());
        preparedStatement.setString(++i, associate.getAdditionalInfo());
        preparedStatement.setObject(++i, associate.getType().toString());
        preparedStatement.setBoolean(++i, true);

        return preparedStatement;
    }

    @Override
    public List<Associate> getAssociatesByType(Long accountId, AssociateType type) {
        try {
            return jdbcTemplate.query(Queries.SQL_SELECT_ASSOCIATES_BY_TYPE, associateRowMapper, accountId, type.name());
        } catch (DataAccessException e) {
            throw new CRUDException("Error during `select * `", e);
        }
    }
    class Queries {

        static final String SQL_ROW_COUNT = """
            SELECT count(id)
            FROM associates
            WHERE account_id = ?
            AND active = true
        """;

        static final String SQL_SELECT_SORTED_ASSOICATES = """
             SELECT *
             FROM associates
             WHERE account_id= ? AND active = true order by %s limit ? offset ?
        """;

        static final String SQL_CREATE_ASSOCIATE = """
            INSERT INTO associates
            ( account_id, name, email, phone, additional_info, type, active)
            VALUES(?,?,?,?,?,?,?)
        """;

        static final String SQL_SELECT_ASSOCIATE_BY_ID = """
            SELECT *
            FROM associates
            WHERE account_id = ? and id = ?
            AND active = true
        """;

        static final String SQL_SELECT_ASSOCIATE_BY_ACCOUNT_ID = """
            SELECT *
            FROM associates
            WHERE account_id = ?
            AND active = true
        """;

        static final String SQL_UPDATE_ASSOCIATE = """
            UPDATE associates
            SET name = ?, email = ?, phone = ?, additional_info = ?
            WHERE account_id = ? and id = ?
        """;

        static final String SQL_SET_ACTIVE_STATUS_ASSOCIATE = """
            UPDATE associates
            SET active = ?
            WHERE account_id = ? and id = ?
        """;
        static final String SQL_SELECT_ASSOCIATES_BY_TYPE = """
                SELECT *
                FROM associates
                 WHERE account_id = ?  AND type = ? AND active = true
            """;
    }
}
